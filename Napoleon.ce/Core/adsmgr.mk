include ../make.vars
include make.vars

PROGRAM=Ads

FORMS=About AdminControl EditTask EditUser FmConfig FmGPSReport FmJournal FmMessage Login \
FmRoute FmUserOrder FmWait FmMain Users TimeGrid TimeGridControl \
EdTxt EdBoolean EdDataSet EdList EdNumber FmMessageHistory FmViewPhoto FmUserLocation FmQuestionary FmQuestEdit FmQuestItemEdit $(MANAGER_FORMS)

F_SRC=$(foreach file,$(FORMS),$(file) $(file).Designer)

SOURCE=AssemblyInfo  Resources.Designer Settings.Designer Excel MapEngine BaseFormSetting  Coordutils DataUtils StringUtil \
Config Objects OpenLink Program VisitQueueItem Comparator Route WeekDay OrgLocation Utils OrgCash TimeGridAdapter \
BandCollection Band BandItem BandItemsCollection TaskContextMenuStrip ItemDraw  HeaderControl Win32 TimeLine \
DialogUtil Objects.Display AgentBand IQuestItem Entries BitmapUtil MapHelper MapObjects JSon TaskHelper EditBuffer \
SelectData $(F_SRC)

RESOURCE=Properties.Resources $(FORMS)

REFERENCE=System.Xml System.Data System System.Windows.Forms System.Drawing System.Design $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(OUT_DIR)/GRPacket))

STARTED_NAMESPACE= GRSoft.NapoleonManager

PRG_ROOT=Napoleon.Net/Ads

PROG_ICO=$(PRG_ROOT)/Resources/napoleon.ico

ProgBase = $(PROJECT)
ifdef VariantBase
ProgBase = $(VariantBase)
endif

SRC_DIR:=Maps UILib Utils Properties Reports/Excel View Controls Modules/Quest

variantPath:=$(PRG_ROOT)/Variants/$(ProgBase) $(foreach srcPath,$(SRC_DIR),$(PRG_ROOT)/Variants/$(ProgBase)/$(srcPath))
sourcePath:=$(PRG_ROOT) $(foreach srcPath,$(SRC_DIR),$(PRG_ROOT)/$(srcPath))

VPATH:=$(variantPath) $(sourcePath)

#$(error $(VPATH))

SOURCE_PATH=$(ROOT)/$(PCORE)/$(PRG_ROOT)/Resources

ifdef MANAGER_SOURCE
  SOURCE += $(MANAGER_SOURCE)
endif

ifdef MANAGER_RESOURCE
  RESOURCE += $(MANAGER_RESOURCE)
endif


all: $(OUT_DIR)/$(PROGRAM).exe

$(INT_DIR)/Properties.Resources.resources: Properties/Resources.resx
	resgen /useSourcePath $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$+)) $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$@))
  
include ../make.cs
