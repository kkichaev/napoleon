@PATH="c:\Program Files (x86)\svn\bin";%PATH%
if "%1" == "" goto end

svn add --depth=empty %1
cd %1
svn add make.vars
svn add makefile
svn add Include

if not exist Changes goto skip_changes

svn add --depth=empty Changes
cd Changes
svn add Add.wxs
svn add addDefs.xml
svn add Formats.doc
svn add GRServer.ini
svn add license
cd ..
svn propset svn:ignore -F ..\ignore_svn Changes

:skip_changes
svn propset svn:ignore "!" .

if exist Reports svn add Reports
if exist Napoleon svn add Napoleon
if exist GRServer svn add GRServer

if not exist Variants goto do_commit

svn add Variants
svn propset svn:ignore -F ..\ignore_svn -R Variants
for /R Variants %%G in (*.upd) DO svn revert "%%G"

:do_commit
if not "%2" == "commit" goto up_dir
svn commit -mmessage ""

:up_dir
cd ..
:end