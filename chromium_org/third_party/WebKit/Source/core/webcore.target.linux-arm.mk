# This file is generated by gyp; do not edit.

include $(CLEAR_VARS)

LOCAL_MODULE_CLASS := GYP
LOCAL_MODULE := third_party_WebKit_Source_core_webcore_gyp
LOCAL_MODULE_STEM := webcore
LOCAL_MODULE_SUFFIX := .stamp
LOCAL_MODULE_TAGS := optional
gyp_intermediate_dir := $(call local-intermediates-dir)
gyp_shared_intermediate_dir := $(call intermediates-dir-for,GYP,shared)

# Make sure our deps are built first.
GYP_TARGET_DEPENDENCIES := \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_WebKit_Source_core_webcore_dom_gyp)/third_party_WebKit_Source_core_webcore_dom_gyp.a \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_WebKit_Source_core_webcore_html_gyp)/third_party_WebKit_Source_core_webcore_html_gyp.a \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_WebKit_Source_core_webcore_platform_gyp)/third_party_WebKit_Source_core_webcore_platform_gyp.a \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_WebKit_Source_core_webcore_platform_geometry_gyp)/third_party_WebKit_Source_core_webcore_platform_geometry_gyp.a \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_WebKit_Source_core_webcore_remaining_gyp)/third_party_WebKit_Source_core_webcore_remaining_gyp.a \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_WebKit_Source_core_webcore_rendering_gyp)/third_party_WebKit_Source_core_webcore_rendering_gyp.a \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_WebKit_Source_core_webcore_svg_gyp)/third_party_WebKit_Source_core_webcore_svg_gyp.a \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_WebKit_Source_core_webcore_wml_gyp)/third_party_WebKit_Source_core_webcore_wml_gyp.a \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_WebKit_Source_core_webcore_derived_gyp)/third_party_WebKit_Source_core_webcore_derived_gyp.a \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_WebKit_Source_wtf_wtf_gyp)/third_party_WebKit_Source_wtf_wtf_gyp.a \
	$(call intermediates-dir-for,GYP,skia_skia_gyp)/skia.stamp \
	$(call intermediates-dir-for,GYP,third_party_npapi_npapi_gyp)/npapi.stamp \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_qcms_qcms_gyp)/third_party_qcms_qcms_gyp.a \
	$(call intermediates-dir-for,STATIC_LIBRARIES,url_url_lib_gyp)/url_url_lib_gyp.a \
	$(call intermediates-dir-for,GYP,v8_tools_gyp_v8_gyp)/v8.stamp \
	$(call intermediates-dir-for,STATIC_LIBRARIES,third_party_WebKit_Source_core_webcore_arm_neon_gyp)/third_party_WebKit_Source_core_webcore_arm_neon_gyp.a

GYP_GENERATED_OUTPUTS :=

# Make sure our deps and generated files are built first.
LOCAL_ADDITIONAL_DEPENDENCIES := $(GYP_TARGET_DEPENDENCIES) $(GYP_GENERATED_OUTPUTS)

### Rules for final target.
# Add target alias to "gyp_all_modules" target.
.PHONY: gyp_all_modules
gyp_all_modules: third_party_WebKit_Source_core_webcore_gyp

# Alias gyp target name.
.PHONY: webcore
webcore: third_party_WebKit_Source_core_webcore_gyp

LOCAL_MODULE_PATH := $(PRODUCT_OUT)/gyp_stamp
LOCAL_UNINSTALLABLE_MODULE := true

include $(BUILD_SYSTEM)/base_rules.mk

$(LOCAL_BUILT_MODULE): $(LOCAL_ADDITIONAL_DEPENDENCIES)
	$(hide) echo "Gyp timestamp: $@"
	$(hide) mkdir -p $(dir $@)
	$(hide) touch $@
