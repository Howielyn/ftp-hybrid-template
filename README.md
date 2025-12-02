# FTP Hybrid Server Template (MINA + FTP Engine Core)

This is a **starter template** for an Android-compatible hybrid FTP server:

- **MINA**: networking layer (replace `mina-shim` with real MINA for production)  
- **FTP Engine Core**: Apache 2.0–friendly FTP engine (filesystem, commands, SAF, user management)  
- **Glue module**: connects MINA networking to FTP Engine Core commands  

## Usage

1. Clone this repository.  
2. Replace `mina-shim` with a real MINA dependency or Android-patched MINA JAR/AAR.  
3. Customize `FTP Engine Core` classes (`FtpFileSystem`, `FtpCommandProcessor`, `FtpUserManager`) for your needs.  
4. Build `sample-app` in Android Studio and run on a device or emulator.  
5. Start the FTP service to accept connections (default port: 2121).  

## Notes

- This is a template scaffold with simplified implementations.  
- SSL/FTPS is **not included** — add carefully if needed.  
- Ensure proper Android permissions for storage access.