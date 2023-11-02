include ../make.vars
include make.vars

PROGRAM=NapoleonMonitor

FORMS=AckName AnswerOverview ChangePassword CheckAdmin DivisionChief Divisions \
 EdBoolean EdDataSet EdList EdNumber EdTxt FmQuestEdit FmQuestionary FmQuestItemEdit FmContractReport \
 FmAddrShow FmAgentOrgTask fmAutoCoef FmCensus FmConfig FmDetailBase FmGPSReport FmMatrixDesigner FmMessage FmRoute FmPriceSetting \
 FmMessageHistory FmOrdersReport FmPhotoRateReport FmPrice FmPricePhoto FmPtnzlOrgEdit FmScriptDesigner FmSelectSKU FmSelectContrAgent FmTask \
 FmTaskAdd FmViewPhoto FmWait FmWelcome FmSVTask FmSetOrgColor FmExportPhoto\
 FmColorEditor FmScriptEdit ReturnOverview FmEditCateg FmProducer FmEditProducer FmQuestionReport FmCateg \
 Login OrderOverview MonitoringItems MonReportParams MainForm RemnantsOverview Route ScriptOverview SelectAgents \
 UserForm ViewException FmCoverArea DatePeriodView ContractOverview $(MONITOR_FORMS)

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

SOURCE=ArticlesTreeConstructor ArticlesTreeConstructorWithCondition FinderTreeNodesInList ClientCard Config ConfigHistory ConfigKeyItems \
  IQuestItem BaseFormSetting OrgLocation \
  CurrentUser DecoratorFactory DivisionForm.Designer Excel ExcelRouteReport ExcelOrderReport IReportFactory RouteReport \
  DivisionForm DivisionList DivisionSummary Entries FmDetail HTMLIncassReport HTMLOrderReport HtmlReport OrderReport ReportData ReportTypes \
  FolderTree IDecorator Objects OpenLink Program VisitQueueItem OVDivision ProgressImage MapEngine TreeSearch\
  AssemblyInfo Resources.Designer Settings.Designer TreeGridCell TreeGridEvents TreeGridNode TreeGridNodeCollection TreeViewMS TreeGridView \
  Comparator Coordutils DataUtils StringUtil WinInetInterop WeekDay OrderDetailRepr ScriptDocuments MonitoringReports \
  AggregateBindingListView BindingListView CompositeItemFilter IItemFilter INotifyingEditableObject InvalidSourceListException MultiSourceIndexList \
  ObjectView ProvidedViewPropertyDescriptor DialogUtil MonitoringOurRep MonitoringConurentRep ScriptDocuments.Init $(F_SRC)

ifeq ($(HaveFocus),1)
SOURCE += FocusedBase
endif

RESOURCE=$(FORMS)

REFERENCE=System.Xml System.Data System System.Windows.Forms System.Drawing System.Design $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(OUT_DIR)/GRPacket))

STARTED_NAMESPACE=GRSoft.NapoleonManager

PRG_ROOT=Napoleon.Net/NapoleonMonitor

PROG_ICO=$(PRG_ROOT)/Resources/napoleon.ico

ProgBase = $(PROJECT)
ifdef VariantBase
ProgBase = $(VariantBase)
endif

SRC_DIR:=Modules/Monitoring Modules/Plans Modules/Quest Maps UILib Utils Utils/BindingList Properties Reports Reports/Html Reports/Excel DBF
ifneq ($(findstring ORG_STOP_EDITOR, $(FEATURES)),)
 SRC_DIR += Modules/OrgStop
endif
ifneq ($(findstring AGENT_ORG_TASK, $(FEATURES)),)
 SRC_DIR += Utils/BindingList
endif

variantPath:=$(PRG_ROOT)/Variants/$(ProgBase) $(PRG_ROOT)/Variants/$(PROJECT) $(foreach srcPath,$(SRC_DIR),$(PRG_ROOT)/Variants/$(ProgBase)/$(srcPath))
sourcePath:=$(PRG_ROOT) $(foreach srcPath,$(SRC_DIR),$(PRG_ROOT)/$(srcPath))

VPATH:=$(variantPath) $(sourcePath)

#$(error $(VPATH))

SOURCE_PATH=$(ROOT)/$(PCORE)/$(PRG_ROOT)/Resources

ifdef MONITOR_SOURCE
  SOURCE += $(MONITOR_SOURCE)
endif

ifdef MONITOR_RESOURCE
  RESOURCE += $(MONITOR_RESOURCE)
endif

ADD_RES_DEP=$(INT_DIR)/Properties.Resources.resources
ADD_RES_STR=/resource:$(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(INT_DIR)/Properties.Resources)).resources,NapoleonMonitor.Properties.Resources.resources

all: $(OUT_DIR)/$(PROGRAM).exe

$(INT_DIR)/Properties.Resources.resources: Properties/Resources.resx
	resgen /useSourcePath $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$+)) $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$@))

include ../make.cs
