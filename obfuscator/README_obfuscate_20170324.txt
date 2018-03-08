=================================================
=     混淆器由 ProGuard 變更為 ZKM 調整步驟     =
=================================================
1. 複製 proguardStub.ZKM.merged.jar 檔案至 {Android Studio安裝目錄]\gradle\m2repository\net\sf\proguard\proguard-base\{版本,如5.3.2}
2. cd {Android Studio安裝目錄]\gradle\m2repository\net\sf\proguard\proguard-base\{版本,如5.3.2}
3. ren proguard-base-5.3.2.jar proguard-base-5.3.2.jar.origin
4. ren proguardStub.ZKM.merged.jar proguard-base-5.3.2.jar


=================================================
= 混淆器由 ProGuard 變更為 ZKM 後, 製作 Filelug =
=================================================
在 Android Studio 中, 執行
1. Clean Project
2. Rebuild Project
3. Generate Signed APK (有時會發生錯誤: com.zelix.j0: Zelix KlassMaster is not thread safe. If you need to run more than once instance concurrently then each instance must run in a separate JVM with different 'java.io.tmpdir' System property values. 再試一次即可)


在檔案總管中, 執行
4. 移至 C:\Workspace\_ASProjects\Working\FilelugApp 資料夾, 剪下檔案 ZKM_PG_log.txt, 貼到 C:\Workspace\_ASProjects\Working\FilelugApp\obfuscator 資料夾
5. 移至 C:\Workspace\_ASProjects\Working\FilelugApp\app\build\outputs\mapping\release 資料夾, 剪下檔案 seeds.txt、usage.txt, 貼到 C:\Workspace\_ASProjects\Working\FilelugApp\obfuscator 資料夾
6. 移至 C:\Workspace\_ASProjects\Working\FilelugApp\app 資料夾, 剪下檔案 app-release.apk, 貼到 C:\Workspace\_ASProjects\Working\FilelugApp\obfuscator 資料夾
7. 移至 C:\Workspace\_ASProjects\Working\FilelugApp\obfuscator 資料夾, 將檔案 app-release.apk 名稱變更為 Filelug.apk
