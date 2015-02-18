# This file is generated by gyp; do not edit.

include $(CLEAR_VARS)

LOCAL_MODULE_CLASS := GYP
LOCAL_MODULE := third_party_WebKit_Source_devtools_concatenated_devtools_codemirror_js_gyp
LOCAL_MODULE_STEM := concatenated_devtools_codemirror_js
LOCAL_MODULE_SUFFIX := .stamp
LOCAL_MODULE_TAGS := optional
gyp_intermediate_dir := $(call local-intermediates-dir)
gyp_shared_intermediate_dir := $(call intermediates-dir-for,GYP,shared)

# Make sure our deps are built first.
GYP_TARGET_DEPENDENCIES :=

### Rules for action "concatenate_devtools_codemirror_js":
$(gyp_shared_intermediate_dir)/resources/inspector/CodeMirrorTextEditor.js: gyp_local_path := $(LOCAL_PATH)
$(gyp_shared_intermediate_dir)/resources/inspector/CodeMirrorTextEditor.js: gyp_intermediate_dir := $(abspath $(gyp_intermediate_dir))
$(gyp_shared_intermediate_dir)/resources/inspector/CodeMirrorTextEditor.js: gyp_shared_intermediate_dir := $(abspath $(gyp_shared_intermediate_dir))
$(gyp_shared_intermediate_dir)/resources/inspector/CodeMirrorTextEditor.js: export PATH := $(subst $(ANDROID_BUILD_PATHS),,$(PATH))
$(gyp_shared_intermediate_dir)/resources/inspector/CodeMirrorTextEditor.js: $(LOCAL_PATH)/third_party/WebKit/Source/devtools/scripts/inline_js_imports.py $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/CodeMirrorTextEditor.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/clike.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/closebrackets.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/codemirror.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/coffeescript.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/comment.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/css.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/htmlembedded.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/htmlmixed.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/javascript.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/markselection.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/matchbrackets.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/overlay.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/php.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/python.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/shell.js $(LOCAL_PATH)/third_party/WebKit/Source/devtools/front_end/cm/xml.js $(GYP_TARGET_DEPENDENCIES)
	@echo "Gyp action: third_party_WebKit_Source_devtools_devtools_gyp_concatenated_devtools_codemirror_js_target_concatenate_devtools_codemirror_js ($@)"
	$(hide)cd $(gyp_local_path)/third_party/WebKit/Source/devtools; mkdir -p $(gyp_shared_intermediate_dir)/resources/inspector; python scripts/inline_js_imports.py front_end/CodeMirrorTextEditor.js front_end "$(gyp_shared_intermediate_dir)/resources/inspector/CodeMirrorTextEditor.js"



GYP_GENERATED_OUTPUTS := \
	$(gyp_shared_intermediate_dir)/resources/inspector/CodeMirrorTextEditor.js

# Make sure our deps and generated files are built first.
LOCAL_ADDITIONAL_DEPENDENCIES := $(GYP_TARGET_DEPENDENCIES) $(GYP_GENERATED_OUTPUTS)

### Rules for final target.
# Add target alias to "gyp_all_modules" target.
.PHONY: gyp_all_modules
gyp_all_modules: third_party_WebKit_Source_devtools_concatenated_devtools_codemirror_js_gyp

# Alias gyp target name.
.PHONY: concatenated_devtools_codemirror_js
concatenated_devtools_codemirror_js: third_party_WebKit_Source_devtools_concatenated_devtools_codemirror_js_gyp

LOCAL_MODULE_PATH := $(PRODUCT_OUT)/gyp_stamp
LOCAL_UNINSTALLABLE_MODULE := true

include $(BUILD_SYSTEM)/base_rules.mk

$(LOCAL_BUILT_MODULE): $(LOCAL_ADDITIONAL_DEPENDENCIES)
	$(hide) echo "Gyp timestamp: $@"
	$(hide) mkdir -p $(dir $@)
	$(hide) touch $@
