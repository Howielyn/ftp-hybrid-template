# FTP Hybrid Server Template (MINA + FTP Engine Core)

This is a **starter template** for an Android-compatible hybrid FTP server.

It combines:

- **MINA**: networking layer (replace `mina-shim` with real MINA for production).  
- **FTP Engine Core**: Apache 2.0–friendly FTP engine (filesystem, commands, SAF, user management).  
- **Glue module**: connects MINA networking to FTP Engine Core commands.

---

## Requirements

- Android Studio 2022+  
- Android SDK 24+  
- Java 17+  
- Gradle 8+  

---

## Module Structure

- **ftp-engine-core**: Core FTP engine (filesystem abstractions, command processor, user management).  
- **mina-android-patched**: Android-compatible clean-room MINA implementation.  
- **ftp-hybrid-server**: Connects MINA networking to FTP Engine Core.  
- **sample-app**: Example Android app demonstrating how to run the FTP server.

---

## Usage

1. Clone this repository.  
2. Replace `mina-shim` with a real MINA dependency or Android-patched MINA JAR/AAR.  
3. Customize `FTP Engine Core` classes (`FtpFileSystem`, `FtpCommandProcessor`, `FtpUserManager`) as needed.  
4. Build `sample-app` in Android Studio and run on a device or emulator.  
5. Start the FTP service:

```java
// Example: start FTP server in an Activity or Service
FtpEngineHybrid ftpServer = new FtpEngineHybrid(this, new SAFFileSystem(context, rootUri));
ftpServer.start(2121); // default port 2121