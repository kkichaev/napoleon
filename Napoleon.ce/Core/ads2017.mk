include ../make.vars
include make.vars

# указан от Core
MS_BUILD_PROJECT=Napoleon.Net/Ads2017/Ads2017.csproj

all: $(OUT_DIR)/$(PROGRAM).exe

include ../make.cs
