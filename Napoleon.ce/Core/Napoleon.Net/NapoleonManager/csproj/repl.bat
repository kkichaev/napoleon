:ls -I repl.bat|xargs sed -i -e "s/<Compile Include=\"FmExportPhoto.cs\">/<Compile Include=\"Forms.v3\\\FmExportPhoto.cs\">\n      <SubType>Form<\/SubType>\n    <\/Compile>\n    <Compile Include=\"FmExportPhoto.Impl.cs\">/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"FmDetailBase.Designer.cs\"/Include=\"Forms.v3\\\FmDetailBase.Designer.cs\"/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"FmDetailBase.resx\"/Include=\"Forms.v3\\\FmDetailBase.resx\"/"

:ls -I repl.bat|xargs sed -i -e "s/<Compile Include=\"FmColorEditor.cs\">/<Compile Include=\"Forms.v3\\\FmColorEditor.cs\">\n      <SubType>Form<\/SubType>\n    <\/Compile>\n    <Compile Include=\"FmColorEditor.Impl.cs\">/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"FmColorEditor.Designer.cs\"/Include=\"Forms.v3\\\FmColorEditor.Designer.cs\"/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"FmColorEditor.resx\"/Include=\"Forms.v3\\\FmColorEditor.resx\"/"

:ls -I repl.bat|xargs sed -i -e "s/<Compile Include=\"FmOrgRadiusDocs.cs\">/<Compile Include=\"Forms.v3\\\FmOrgRadiusDocs.cs\">\n      <SubType>Form<\/SubType>\n    <\/Compile>\n    <Compile Include=\"FmOrgRadiusDocs.Impl.cs\">/"
ls -I repl.bat|xargs sed -i -e "s/Include=\"FmAgentTaskList.designer.cs\"/Include=\"Forms.v3\\\FmAgentTaskList.Designer.cs\"/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"FmOrgRadiusDocs.resx\"/Include=\"Forms.v3\\\FmOrgRadiusDocs.resx\"/"

:ls -I repl.bat|xargs sed -i -e "s/<Compile Include=\"FmScriptDesigner.cs\">/<Compile Include=\"Forms.v3\\\FmScriptDesigner.cs\">\n      <SubType>Form<\/SubType>\n    <\/Compile>\n    <Compile Include=\"FmScriptDesigner.Impl.cs\">/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"FmScriptDesigner.Designer.cs\"/Include=\"Forms.v3\\\FmScriptDesigner.Designer.cs\"/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"FmScriptDesigner.resx\"/Include=\"Forms.v3\\\FmScriptDesigner.resx\"/"

:ls -I repl.bat|xargs sed -i -e "s/<Compile Include=\"FmCoverArea.cs\">/<Compile Include=\"Forms.v3\\\FmCoverArea.cs\">\n      <SubType>Form<\/SubType>\n    <\/Compile>\n    <Compile Include=\"FmCoverArea.Impl.cs\">/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"FmCoverArea.Designer.cs\"/Include=\"Forms.v3\\\FmCoverArea.Designer.cs\"/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"FmCoverArea.resx\"/Include=\"Forms.v3\\\FmCoverArea.resx\"/"

:ls -I repl.bat|xargs sed -i -e "s/<Compile Include=\"Divisions.cs\">/<Compile Include=\"Forms.v3\\\Divisions.cs\">\n      <SubType>Form<\/SubType>\n    <\/Compile>\n    <Compile Include=\"Divisions.Impl.cs\">/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"Divisions.Designer.cs\"/Include=\"Forms.v3\\\Divisions.Designer.cs\"/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"Divisions.resx\"/Include=\"Forms.v3\\\Divisions.resx\"/"

:ls -I repl.bat|xargs sed -i -e "s/<Compile Include=\"Modules\\\Quest\\\FmQuestEdit.cs\">/<Compile Include=\"Forms.v3\\\FmQuestEdit.cs\">\n      <SubType>Form<\/SubType>\n    <\/Compile>\n    <Compile Include=\"Modules\\\Quest\\\FmQuestEdit.Impl.cs\">/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"Modules\\\Quest\\\FmQuestEdit.Designer.cs\"/Include=\"Forms.v3\\\FmQuestEdit.Designer.cs\"/"
:ls -I repl.bat|xargs sed -i -e "s/Include=\"Modules\\\Quest\\\FmQuestEdit.resx\"/Include=\"Forms.v3\\\FmQuestEdit.resx\"/"

:ls -I repl.bat|xargs sed -i -e "s/<EmbeddedResource Include=\"Properties.v3\\\Resources.resx\">/\0\n      <CustomToolNamespace>GRSoft.NapoleonManager.Properties<\/CustomToolNamespace>/"
:ls -I repl.bat|xargs sed -i -e "s/Resources\\\/Resources.v3\\\/"
:end
