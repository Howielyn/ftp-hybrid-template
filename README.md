# FTP Hybrid Server - MINA + Swiftp (Template)

This repository is a **starter GitHub template** for an Android-compatible hybrid FTP server that combines:

- **MINA** (networking layer) — *intended to be used via an external dependency or replaced with a real MINA artifact*  
- **Swiftp** (Android FTP engine: filesystem, commands, SAF/perms, user management) — included here as simplified core classes  
- **Glue module** that wires MINA networking to Swiftp command processing

> ⚠️ This is a template scaffold. It contains simplified/shim implementations (mina-shim) so you can build and experiment locally. Replace `mina-shim` with the real MINA dependency or your Android-patched MINA jar/aar when ready.

## Project layout

```
/
|- mina-shim/               # Small interface stubs for MINA types (replace with real MINA)
|- swiftp-core/            # Simplified Swiftp-inspired FTP engine (Android-friendly)
|- ftp-hybrid-server/      # Glue: MINA IoHandler connecting to Swiftp command processor
|- sample-app/             # Minimal Android app demonstrating starting the server (Service)
|- README.md
|- LICENSE
```

## How to use

1. Clone this repo to your machine.
2. Replace `mina-shim` module with a real MINA dependency (or produce an Android-compatible MINA AAR/JAR and add it as a module or dependency).
   - You can keep the same class names for easy swap.
3. Implement or refine the Swiftp classes (`AndroidFileSystem`, `SwiftpCommandProcessor`) to match your needs (SAF / external storage).
4. Build the `sample-app` with Android Studio and run on device/emulator (note: real external storage access requires proper Android permissions).
5. Start the FTP service from the sample app to accept FTP connections.

## Quick notes

- The sample `FtpService` starts the hybrid server bound to port **2121** by default.
- This template intentionally avoids complex MINA internals and SSL/FTPS. Add SSL support carefully for Android.
- See `ftp-hybrid-server/src/main/java/.../FtpHybridServer.java` for the precise glue code.

## Next steps I can help with

- Replace `mina-shim` with a patched MINA core suitable for Android.
- Flesh out Swiftp command set with full RFC-compliant handling.
- Add Passive mode data connection using MINA or java.net sockets.
- Create a ready-to-publish AAR artifact for the hybrid server.

Happy hacking! 🚀
