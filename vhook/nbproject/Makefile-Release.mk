#
# Generated Makefile - do not edit!
#
# Edit the Makefile in the project folder instead (../Makefile). Each target
# has a -pre and a -post target defined where you can add customized code.
#
# This makefile implements configuration specific macros and targets.


# Environment
MKDIR=mkdir
CP=cp
CCADMIN=CCadmin
RANLIB=ranlib
CC=gcc.exe
CCC=g++.exe
CXX=g++.exe
FC=
AS=as.exe

# Macros
CND_PLATFORM=MinGW_TDM_1-Windows
CND_CONF=Release
CND_DISTDIR=dist

# Include project Makefile
include Makefile

# Object Directory
OBJECTDIR=build/${CND_CONF}/${CND_PLATFORM}

# Object Files
OBJECTFILES= \
	${OBJECTDIR}/niwango/node/prefix_op_node.o \
	${OBJECTDIR}/niwango/node/args_node.o \
	${OBJECTDIR}/niwango/node/op_assign_node.o \
	${OBJECTDIR}/niwango/node/array_node.o \
	${OBJECTDIR}/niwango/node/suffix_op_node.o \
	${OBJECTDIR}/niwango_test.o \
	${OBJECTDIR}/niwango/node/literal_node.o \
	${OBJECTDIR}/niwango/node/node.o \
	${OBJECTDIR}/niwango/node/assign_node.o \
	${OBJECTDIR}/niwango/node/label_node.o \
	${OBJECTDIR}/niwango/node/calling_node.o \
	${OBJECTDIR}/niwango/node/accessor_node.o \
	${OBJECTDIR}/niwango/parser/NiwangoLexer.o \
	${OBJECTDIR}/niwango/parser/NiwangoParser.o \
	${OBJECTDIR}/niwango/node/top_level_node.o \
	${OBJECTDIR}/niwango/node/binary_op_node.o

# C Compiler Flags
CFLAGS=

# CC Compiler Flags
CCFLAGS=
CXXFLAGS=

# Fortran Compiler Flags
FFLAGS=

# Assembler Flags
ASFLAGS=

# Link Libraries and Options
LDLIBSOPTIONS=-L/X/bin/msys/app/core/lib -static -lmingw32 -lm -lglib-2.0 -lintl -lole32 -lpthread -liconv

# Build Targets
.build-conf: ${BUILD_SUBPROJECTS}
	${MAKE}  -f nbproject/Makefile-Release.mk dist/Release/MinGW_TDM_1-Windows/libvhook.dll

dist/Release/MinGW_TDM_1-Windows/libvhook.dll: ${OBJECTFILES}
	${MKDIR} -p dist/Release/MinGW_TDM_1-Windows
	${LINK.c} -shared -o ${CND_DISTDIR}/${CND_CONF}/${CND_PLATFORM}/libvhook.dll -s ${OBJECTFILES} ${LDLIBSOPTIONS} 

${OBJECTDIR}/niwango/node/prefix_op_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/prefix_op_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/prefix_op_node.o niwango/node/prefix_op_node.c

${OBJECTDIR}/niwango/node/args_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/args_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/args_node.o niwango/node/args_node.c

${OBJECTDIR}/niwango/node/op_assign_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/op_assign_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/op_assign_node.o niwango/node/op_assign_node.c

${OBJECTDIR}/niwango/node/array_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/array_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/array_node.o niwango/node/array_node.c

${OBJECTDIR}/niwango/node/suffix_op_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/suffix_op_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/suffix_op_node.o niwango/node/suffix_op_node.c

${OBJECTDIR}/niwango_test.o: nbproject/Makefile-${CND_CONF}.mk niwango_test.c 
	${MKDIR} -p ${OBJECTDIR}
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango_test.o niwango_test.c

${OBJECTDIR}/niwango/node/literal_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/literal_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/literal_node.o niwango/node/literal_node.c

${OBJECTDIR}/niwango/node/node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/node.o niwango/node/node.c

${OBJECTDIR}/niwango/node/assign_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/assign_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/assign_node.o niwango/node/assign_node.c

${OBJECTDIR}/niwango/node/label_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/label_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/label_node.o niwango/node/label_node.c

${OBJECTDIR}/niwango/node/calling_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/calling_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/calling_node.o niwango/node/calling_node.c

${OBJECTDIR}/niwango/node/accessor_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/accessor_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/accessor_node.o niwango/node/accessor_node.c

${OBJECTDIR}/niwango/parser/NiwangoLexer.o: nbproject/Makefile-${CND_CONF}.mk niwango/parser/NiwangoLexer.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/parser
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/parser/NiwangoLexer.o niwango/parser/NiwangoLexer.c

${OBJECTDIR}/niwango/parser/NiwangoParser.o: nbproject/Makefile-${CND_CONF}.mk niwango/parser/NiwangoParser.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/parser
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/parser/NiwangoParser.o niwango/parser/NiwangoParser.c

${OBJECTDIR}/niwango/node/top_level_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/top_level_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/top_level_node.o niwango/node/top_level_node.c

${OBJECTDIR}/niwango/node/binary_op_node.o: nbproject/Makefile-${CND_CONF}.mk niwango/node/binary_op_node.c 
	${MKDIR} -p ${OBJECTDIR}/niwango/node
	${RM} $@.d
	$(COMPILE.c) -O2 -I/X/bin/msys/app/core/include -I/X/bin/msys/app/core/include/glib-2.0 -I/X/bin/msys/app/core/lib/glib-2.0/include  -MMD -MP -MF $@.d -o ${OBJECTDIR}/niwango/node/binary_op_node.o niwango/node/binary_op_node.c

# Subprojects
.build-subprojects:

# Clean Targets
.clean-conf: ${CLEAN_SUBPROJECTS}
	${RM} -r build/Release
	${RM} dist/Release/MinGW_TDM_1-Windows/libvhook.dll

# Subprojects
.clean-subprojects:

# Enable dependency checking
.dep.inc: .depcheck-impl

include .dep.inc
