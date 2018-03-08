=================================================
=     混淆器由 ProGuard 變更為 ZKM 調整步驟     =
=================================================
1. 複製 proguardStub.jar、ZKM.jar 兩個檔案至 {Android Studio安裝目錄]\gradle\m2repository\net\sf\proguard\proguard-base\{版本,如5.2.1}
2. cd {Android Studio安裝目錄]\gradle\m2repository\net\sf\proguard\proguard-base\{版本,如5.2.1}
3. ren proguard-base-5.2.1.jar proguard-base-5.2.1.jar.origin
4. ren proguardStub.jar proguard-base-5.2.1.jar


=================================================
= 混淆器由 ProGuard 變更為 ZKM 後, 製作 Filelug =
=================================================
在 Android Studio 中, 執行
1. Clean Project
2. Rebuild Project
3. Generate Signed APK (會在混淆時發生錯誤)

在檔案總管中, 執行
4. 複製 C:\Workspace\_ASProjects\Working\FilelugApp\app\build\outputs\mapping\release\seeds.txt,
   並將檔案貼到 C:\Workspace\_ASProjects\Working\FilelugApp\obfuscator
5. 將上述檔案 seeds.txt 更名為 ZKM.script
6. 編輯 ZKM.script 檔案, 僅留 ZKM 混淆相關設定其他移除
7. 修改 obfuscate 設定,
	a. methodParameters: keepVisibleIfNotObfuscated --> obfuscate
	b. changeLogFileOut --> "C:\Workspace\_ASProjects\Working\FilelugApp\obfuscator\ZKM_Log_Mapping.txt"
	c. obfuscateFlow: none --> light
	d. encryptStringLiterals: none --> flowObfuscate
	//e. exceptionObfuscation: none --> light
	f. randomize: false --> true

在命令提示列中, 執行
8. cd C:\Workspace\_ASProjects\Working\FilelugApp\obfuscator
9. del ZKM_*.txt
10. java -DZKM_IGNORE_MISSING_MEMBERS=true -jar ZKM.jar -v -tl C:\Workspace\_ASProjects\Working\FilelugApp\obfuscator\ZKM_Log_Trim.txt -l C:\Workspace\_ASProjects\Working\FilelugApp\obfuscator\ZKM_Log.txt ZKM.script

在命令提示列中, 執行
11. cd C:\Workspace\_ASProjects\Working\FilelugApp
12. set GRADLE_HOME=C:\Android\Android Studio\gradle\gradle-2.14.1
13. set PATH=%PATH%;%GRADLE_HOME%\bin
14. gradle app:assembleRelease -x app:transformClassesAndResourcesWithProguardForRelease

在命令提示列中, 執行
15. cd C:\Workspace\_ASProjects\Working\FilelugApp\app\build\outputs\apk
16. copy app-release.apk Filelug.apk
