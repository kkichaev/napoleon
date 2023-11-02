SHELL=$(ROOT)/shell

include $(ROOT)/make.def

ifndef VERSION
ifneq ($(findstring VERSION_4, $(FEATURES)),)
   ifneq ($(findstring VERSION_5, $(FEATURES)),)
     VERSION=5.0.0.15
   else
     VERSION=4.0.0.14
   endif
else
VERSION=3.5.0.12
endif
endif

FLAGS=/noconfig /nowarn:1701,1702 /errorreport:prompt /warn:4 /define:TRACE /filealign:512 $(CS_FLAGS)
ifdef CS_PLATFORM
FLAGS+=/platform:$(CS_PLATFORM)
endif

ifdef TEST
FLAGS+=/define:TEST
endif

ifdef PROG_ICO
FLAGS+=/win32icon:$(PROG_ICO)
endif

ifdef DEBUG
FLAGS+=/debug /define:DEBUG
else
FLAGS+=/optimize+ 
endif

ifndef ASSEMBLY_PROJECT
ifdef VariantBase
ASSEMBLY_PROJECT = $(VariantBase)
else
ASSEMBLY_PROJECT = $(PROJECT)
endif
endif

#c:\Windows\Microsoft.NET\Framework\v2.0.50727

ifndef MS_BUILD_PROJECT
#DOT_NET_PATH=c:\Windows\Microsoft.NET\Framework\v3.5
DOT_NET_PATH=C:\Program Files (x86)\Microsoft Visual Studio\2019\Community\MSBuild\Current\Bin\Roslyn
else
DOT_NET_PATH=c:\Program Files (x86)\Microsoft Visual Studio\2017\Community\MSBuild\15.0\Bin
endif

ifdef DEBUG
BUILD_CONFIG=Debug
else
BUILD_CONFIG=Release
endif
BUILD_PATH=$(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(OUT_DIR)))
BUILD_OBJ_PATH=$(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(OUT_DIR)/$(PROJECT)/))

ResGenPath = C:\Program Files (x86)\Microsoft SDKs\Windows\v8.1A\bin\NETFX 4.5.1 Tools
#ResGenPath = c:\Program Files\Microsoft SDKs\Windows\v6.0A\bin

T_PATH=$(DOT_NET_PATH);$(ResGenPath);c:\Works\Cygwin\bin;/cygdrive/c/Works/Python39;
export PATH:=$(subst ;,:,$(subst D:,/cygdrive/d,$(subst d:,/cygdrive/d,$(subst C:,/cygdrive/c,$(subst c:,/cygdrive/c,$(T_PATH))))))

dummy := $(shell mkdir -p "$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(INT_DIR))")


REF_STR=$(foreach file,$(REFERENCE),/reference:$(file).dll)

RES_STR=$(foreach fileName,$(RESOURCE),/res:$(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(INT_DIR)/$(fileName))).resources,$(STARTED_NAMESPACE).$(fileName).resources)

$(INT_DIR)/%.resources: %.resx
	resgen /useSourcePath $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$+)) $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$@))

ifndef MS_BUILD_PROJECT
$(OUT_DIR)/$(PROGRAM).dll: $(foreach fileName,$(SOURCE),$(fileName).cs)
	csc /nowarn:1591 $(FLAGS) /target:library $(REF_STR) $(RES_STR) /out:$(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$@)) $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$+))
	rm AssemblyInfo.cs
endif

#$(foreach fileName,$(SOURCE),$(fileName).cs) $(foreach fileName,$(RESOURCE),$(INT_DIR)/$(fileName).resources) $(ADD_RES_DEP)
#	csc /nowarn:1591 $(FLAGS) /target:winexe $(REF_STR) $(RES_STR) $(ADD_RES_STR) /out:$(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$@)) $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(filter %.cs,$+)))

ifndef MS_BUILD_PROJECT
$(OUT_DIR)/$(PROGRAM).exe: $(OUT_DIR)/$(PROGRAM).rsp
	csc /nowarn:1591 $(FLAGS) /target:winexe $(REF_STR) $(ADD_RES_STR) /out:$(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$@)) @$(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(OUT_DIR)/$(PROGRAM).rsp))
else
$(OUT_DIR)/$(PROGRAM).exe:
	msbuild $(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(ROOT)/$(NAPOLEON_CORE)/$(MS_BUILD_PROJECT))) /tv:"15.0" /p:Configuration=$(BUILD_CONFIG);OutputPath=$(BUILD_PATH);IntermediateOutputPath=$(BUILD_OBJ_PATH);DefineConstants="$(DefineConstants)"
endif

$(OUT_DIR)/$(PROGRAM).rsp: $(foreach fileName,$(SOURCE),$(fileName).cs) $(foreach fileName,$(RESOURCE),$(INT_DIR)/$(fileName).resources) $(ADD_RES_DEP)
	$(file > $(OUT_DIR)/$(PROGRAM).rsp,)
	$(file >> $(OUT_DIR)/$(PROGRAM).rsp,$(RES_STR))
	$(file >> $(OUT_DIR)/$(PROGRAM).rsp,$(subst /,\,$(subst $(CYG_DRIVE),$(WIN_DRIVE),$(filter %.cs,$+))))

ifndef MS_BUILD_PROJECT
AssemblyInfo.cs: FORCE
	python $(subst $(CYG_DRIVE),$(WIN_DRIVE),$(ROOT)/makeai.py) $(subst $(CYG_DRIVE),$(WIN_DRIVE),$@) $(ASSEMBLY_PROJECT) $(VERSION) $(PROGRAM)

FORCE:
endif