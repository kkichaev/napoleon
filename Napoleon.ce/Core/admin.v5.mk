include ../make.vars
include make.vars

PROGRAM=NapoleonAdmin

ifdef TEST
PROGRAM=NapoleonAdminTest
endif

FORMS=$(ADMIN_FORMS)
F_SRC=$(foreach file,$(FORMS),$(file) $(file).Designer)

SOURCE=Config Comparator Entries MainForm MainForm.Designer \
 Objects Program AssemblyInfo Resources.Designer $(ADMIN_SOURCE) $(F_SRC)

RESOURCE=Properties.Resources MainForm Resources $(ADMIN_RESOURCE) $(FORMS)

REFERENCE=System.Xml System.Data System System.Windows.Forms System.Drawing System.Design $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(OUT_DIR)/GRPacket))

STARTED_NAMESPACE=GRSoft.NapoleonAdmin

PRG_ROOT=Napoleon.Net/NapoleonAdmin.v5
PROG_ICO=$(PRG_ROOT)/Resources/admin.ico

SRC_DIR:=Properties

ProgBase = $(PROJECT)
ifdef VariantBase
ProgBase = $(VariantBase)
endif

variantPath:=$(PRG_ROOT)/Variants/$(ProgBase) $(foreach srcPath,$(SRC_DIR),$(PRG_ROOT)/Variants/$(ProgBase)/$(srcPath))
sourcePath:=$(PRG_ROOT) $(foreach srcPath,$(SRC_DIR),$(PRG_ROOT)/$(srcPath))

VPATH:=$(variantPath) $(sourcePath)

all: $(OUT_DIR)/$(PROGRAM).exe

$(INT_DIR)/Properties.Resources.resources: Properties/Resources.resx
	resgen /useSourcePath $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$+)) $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$@))
include ../make.cs


