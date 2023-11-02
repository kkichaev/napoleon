include ../make.vars
include make.vars

ifdef TEST
PROGRAM=NapoleonManagerTest
endif

ifdef ManagerVersion
VERSION=$(ManagerVersion)
endif

ifndef PROGRAM
PROGRAM=NapoleonManager
endif

FORMS=AckName AnswerOverview DivisionChief Divisions \
 EdBoolean EdDataSet EdList EdNumber EdNumberList EdTxt FmQuestEdit FmQuestionary FmQuestItemEdit FmExportPhoto FmOrgRadiusDocs \
 FmAddrShow FmAgentOrgTask fmAutoCoef FmCensus FmConfig FmCopyRoute FmDetailBase FmGPSReport FmMatrixDesigner FmMessage FmRoute FmPriceSetting \
 FmMessageHistory FmOrdersReport FmPhotoRateReport FmPrice FmPricePhoto FmPtnzlOrgEdit FmScriptDesigner FmSelectSKU FmSelectContrAgent FmTask \
 FmTaskAdd FmViewPhoto FmWait FmSVTask FmSetOrgColor FmRouteHistory \
 FmColorEditor FmScriptEdit ReturnOverview FmEditCateg FmProducer FmEditProducer FmQuestionReport FmCateg \
 OrderOverview MonitoringItems MonReportParams MainForm MoneyProxyDetail RemnantsOverview Route ScriptOverview SelectAgents \
 UserForm ViewException FmCoverArea DatePeriodView FmTaskReport WebViewWarning\
 WorkTimeParams FmRouteApproval FmApproveMsg FmScriptTimeRptParam FmUserLocation FmQuestAttach EdSpinner EdImage $(MANAGER_FORMS)

FORMS += FmReports RichButton FmSelectOrgs 


ifneq ($(findstring FOCUSED_GROUP, $(FEATURES)),)
FORMS+=FocusedGroupEditor
HaveFocus:=1
endif

ifneq ($(findstring FOCUSED_ITEMS, $(FEATURES)),)
FORMS+=FocusedItemsEditor
HaveFocus:=1
endif

ifneq ($(findstring SELECT_ORG_LOCATION, $(FEATURES)),)
FORMS+=SelectOrgLocation
HaveFocus:=1
endif

ifneq ($(findstring ORG_STOP_EDITOR, $(FEATURES)),)
 FORMS+=FmStopOrgList
endif

ifneq ($(findstring AGENT_ORG_TASK, $(FEATURES)),)
 FORMS+=FmAgentTask FmAgentTaskEdit FmAgentTaskList
endif

F_SRC=$(foreach file,$(FORMS),$(file) $(file).Designer)

ifneq ($(findstring USE_MANAGER_LOG, $(FEATURES)),)
 F_SRC += Log
endif

F_SRC += Divisions.Impl FmCoverArea.Impl FmDetailBase.Impl FmMatrixDesigner.Impl FmPrice.Impl FmQuestionary.Impl 
F_SRC += FmRoute.Impl MainForm.Impl Route.Impl FmCensus.Impl FmPricePhoto.Impl FmScriptDesigner.Impl FmOrgRadiusDocs.Impl 
F_SRC += FmColorEditor.Impl FmQuestEdit.Impl DivisionChief.Impl FmScriptEdit.Impl 

SOURCE += DivisionChief.Impl

ifneq ($(findstring AGENT_ORG_TASK, $(FEATURES)),)
F_SRC += FmAgentTask.Impl FmAgentTaskList.Impl 
endif

SOURCE=ArticlesTreeConstructor ArticlesTreeConstructorWithCondition CheckedComboBox FinderTreeNodesInList ClientCard Config ConfigKeyItems \
  IQuestItem BaseFormSetting DBFBase DBFException DBFField DBFHeader DBFReader DBFUtils DBFValue DBFWriter OrgLocation\
  CurrentUser DecoratorFactory DivisionForm.Designer Excel ExcelRouteReport ExcelOrderReport IReportFactory RouteReport \
  DivisionForm DivisionList DivisionSummary Entries FmDetail HTMLIncassReport HTMLOrderReport HtmlReport OrderReport ReportData ReportTypes \
  FolderTree IDecorator Objects OpenLink Program VisitQueueItem OVDivision ProgressImage MapEngine TreeSearch\
  AssemblyInfo Resources.Designer Settings.Designer TreeGridCell TreeGridEvents TreeGridNode TreeGridNodeCollection TreeViewMS TreeGridView \
  Comparator Coordutils DataUtils StringUtil WinInetInterop WeekDay OrderDetailRepr ScriptDocuments MonitoringReports \
  AggregateBindingListView BindingListView CompositeItemFilter IItemFilter INotifyingEditableObject InvalidSourceListException MultiSourceIndexList \
  ObjectView ProvidedViewPropertyDescriptor DialogUtil MonitoringOurRep MonitoringConurentRep ScriptDocuments.Init ReportResult \
  WorkTimeReport ScriptTimeRpt WebView $(F_SRC)

ifeq ($(HaveFocus),1)
SOURCE += FocusedBase
endif

RESOURCE=Properties.Resources $(FORMS)


STARTED_NAMESPACE=GRSoft.NapoleonManager

PRG_ROOT=Napoleon.Net/NapoleonManager.v5

WebView = $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(ROOT)/$(PCORE)/References/WebView/))
REFERENCE=System.Xml System.Data System System.Windows.Forms System.Drawing System.Design System.Core $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(OUT_DIR)/GRPacket)) $(WebView)Microsoft.Web.WebView2.Core $(WebView)Microsoft.Web.WebView2.WinForms

ProgBase = $(PROJECT)
ifdef VariantBase
ProgBase = $(VariantBase)
endif

SRC_DIR:=Modules/Monitoring Modules/Plans Modules/Quest Maps UILib Utils Utils/BindingList Reports Reports/Html Reports/Excel DBF

VPATH := $(PRG_ROOT)/Forms.v4
SOURCE_PATH=$(ROOT)/$(PCORE)/$(PRG_ROOT)/Resources.v4
SRC_DIR+= Properties.v4
PROG_ICO=$(PRG_ROOT)/Resources.v4/napoleon.ico

ifneq ($(findstring ORG_STOP_EDITOR, $(FEATURES)),)
 SRC_DIR += Modules/OrgStop
endif
ifneq ($(findstring AGENT_ORG_TASK, $(FEATURES)),)
 SRC_DIR += Utils/BindingList
endif

#variantPath:=$(PRG_ROOT)/Variants/$(ProgBase) $(PRG_ROOT)/Variants/$(PROJECT) $(foreach srcPath,$(SRC_DIR),$(PRG_ROOT)/Variants/$(ProgBase)/$(srcPath))
sourcePath:=$(PRG_ROOT) $(foreach srcPath,$(SRC_DIR),$(PRG_ROOT)/$(srcPath))

VPATH+=$(variantPath) $(sourcePath)

#$(error $(VPATH))


ifdef MANAGER_SOURCE
  SOURCE += $(MANAGER_SOURCE)
endif

ifdef MANAGER_RESOURCE
  RESOURCE += $(MANAGER_RESOURCE)
endif

ifdef EXCEL_LIBRARY
 REFERENCE += $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(OUT_DIR)/ExcelLibrary))
endif

all: $(if $(EXCEL_LIBRARY), $(OUT_DIR)/ExcelLibrary.dll) $(OUT_DIR)/$(PROGRAM).exe

$(INT_DIR)/Properties.Resources.resources: Resources.resx
	resgen /useSourcePath $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$+)) $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$@))

$(OUT_DIR)/ExcelLibrary.dll: References/ExcelLibrary.dll 
	cp $^ $@ 

include ../make.cs
