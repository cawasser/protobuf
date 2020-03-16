
@ECHO OFF

set OUT_DIR=docs\current\javadoc

RMDIR /S /Q %OUT_DIR%

javadoc ^
  -d %OUT_DIR% ^
  -public protobuf ^
  -windowtitle "Clojure Protocol Buffer Library" ^
  src\java\protobuf/*.java