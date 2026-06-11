"use strict";

const { app, BrowserWindow, Menu } = require("electron");
const { spawn } = require("child_process");
const http = require("http");
const path = require("path");
const fs = require("fs");
const handler = require("serve-handler");

const BACKEND_PORT = 8080;
const FRONT_PORT = 8090;

let javaProc = null;
let staticServer = null;
let mainWindow = null;

function resolvePaths() {
  if (!app.isPackaged) {
    // Development: use the repo layout and the system JVM.
    const root = path.join(__dirname, "..");
    return {
      jar: path.join(root, "build", "libs", "sgvac-0.0.1-SNAPSHOT.jar"),
      out: path.join(root, "frontend", "out"),
      javaBin: "java",
      // Machine-specific JDK workaround (AF_UNIX); set only where needed.
      patchDir: process.env.SGVAC_JDK_PATCH || null,
    };
  }
  // Packaged: everything lives under resources/.
  const res = process.resourcesPath;
  const bundledPatch = path.join(res, "jdkpatch", "classes");
  return {
    jar: path.join(res, "backend", "sgvac.jar"),
    out: path.join(res, "frontend"),
    javaBin: path.join(res, "jre", "bin", "java.exe"),
    patchDir: fs.existsSync(bundledPatch) ? bundledPatch : null,
  };
}

function startBackend(paths) {
  const args = [];
  if (paths.patchDir && fs.existsSync(paths.patchDir)) {
    args.push(`--patch-module=java.base=${paths.patchDir}`);
  }
  args.push("-Dspring.profiles.active=desktop", "-jar", paths.jar);
  console.log("[backend]", paths.javaBin, args.join(" "));
  javaProc = spawn(paths.javaBin, args, { stdio: "inherit" });
  javaProc.on("exit", (code) => console.log("[backend] exited", code));
}

function startStaticServer(paths) {
  return new Promise((resolve, reject) => {
    staticServer = http.createServer((req, res) =>
      handler(req, res, { public: paths.out, cleanUrls: true, trailingSlash: true })
    );
    staticServer.on("error", reject);
    staticServer.listen(FRONT_PORT, "127.0.0.1", resolve);
  });
}

function waitForBackend(timeoutMs = 90000) {
  const deadline = Date.now() + timeoutMs;
  return new Promise((resolve, reject) => {
    const tryOnce = () => {
      const req = http.get(
        { host: "127.0.0.1", port: BACKEND_PORT, path: "/login", timeout: 2000 },
        (res) => {
          res.destroy();
          resolve();
        }
      );
      req.on("error", retry);
      req.on("timeout", () => {
        req.destroy();
        retry();
      });
    };
    const retry = () => {
      if (Date.now() > deadline) return reject(new Error("backend timeout"));
      setTimeout(tryOnce, 1000);
    };
    tryOnce();
  });
}

async function createWindow() {
  mainWindow = new BrowserWindow({
    width: 1280,
    height: 860,
    backgroundColor: "#1C1C1E",
    title: "SGVAC",
    show: false,
    webPreferences: { contextIsolation: true },
  });
  Menu.setApplicationMenu(null);
  await mainWindow.loadURL(`http://localhost:${FRONT_PORT}/login/`);
  mainWindow.show();
}

app.whenReady().then(async () => {
  const paths = resolvePaths();
  startBackend(paths);
  await startStaticServer(paths);
  try {
    await waitForBackend();
  } catch (e) {
    console.error("[backend] not ready:", e.message);
  }
  await createWindow();

  app.on("activate", () => {
    if (BrowserWindow.getAllWindows().length === 0) createWindow();
  });
});

function shutdown() {
  if (javaProc) {
    javaProc.kill();
    javaProc = null;
  }
  if (staticServer) {
    staticServer.close();
    staticServer = null;
  }
}

app.on("window-all-closed", () => {
  shutdown();
  app.quit();
});

app.on("before-quit", shutdown);
