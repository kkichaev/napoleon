include ../make.vars
include make.vars

ifdef AdminVersion
VERSION=$(AdminVersion)
endif


ifdef TEST
PROGRAM=NapoleonAdminTest
else
PROGRAM=NapoleonAdmin
endif

FORMS=$(ADMIN_FORMS) RmvScheduler
F_SRC=$(foreach file,$(FORMS),$(file) $(file).Designer)

SOURCE=AgentEdit AgentEdit.Designer AskPassword AskPassword.Designer Config Comparator Entries Login Login.designer MainForm MainForm.Designer \
 Objects Program AssemblyInfo UserData UserDataItem UserLogData Resources.Designer ConfigHistory ReportResult $(ADMIN_SOURCE) $(F_SRC)

RESOURCE=Properties.Resources AgentEdit AskPassword Login MainForm Resources $(ADMIN_RESOURCE) $(FORMS)

REFERENCE=System.Xml System.Data System System.Windows.Forms System.Drawing System.Design $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(OUT_DIR)/GRPacket))

STARTED_NAMESPACE=GRSoft.NapoleonAdmin

PRG_ROOT=Napoleon.Net/NapoleonAdmin
PROG_ICO=$(PRG_ROOT)/Resources/admin.ico

SRC_DIR:=Properties

ProgBase = $(PROJECT)
ifdef VariantBase
ProgBase = $(VariantBase)
endif

variantPath:=$(PRG_ROOT)/Variants/$(ProgBase) $(foreach srcPath,$(SRC_DIR),$(PRG_ROOT)/Variants/$(ProgBase)/$(srcPath))
sourcePath:=$(PRG_ROOT) $(foreach srcPath,$(SRC_DIR),$(PRG_ROOT)/$(srcPath))

VPATH:=$(variantPath) $(sourcePath)

ifdef EXCEL_LIBRARY_ADMIN
  REFERENCE += $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(OUT_DIR)/ExcelLibrary))
endif

all: $(if $(EXCEL_LIBRARY_ADMIN), $(OUT_DIR)/ExcelLibrary.dll) $(OUT_DIR)/$(PROGRAM).exe

$(INT_DIR)/Properties.Resources.resources: Properties/Resources.resx
	resgen /useSourcePath $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$+)) $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$@))

$(OUT_DIR)/ExcelLibrary.dll: References/ExcelLibrary.dll 
	cp $^ $@ 
	
include ../make.cs


