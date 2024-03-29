// Copyright 2011 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "cc/trees/layer_tree_settings.h"

#include <limits>

#include "base/command_line.h"
#include "base/logging.h"
#include "base/strings/string_number_conversions.h"

/// M: set num_raster_threads as property
#include <cutils/properties.h>

namespace cc {

LayerTreeSettings::LayerTreeSettings()
    : impl_side_painting(false),
      allow_antialiasing(true),
      throttle_frame_production(true),
      begin_frame_scheduling_enabled(false),
      using_synchronous_renderer_compositor(false),
      per_tile_painting_enabled(false),
      partial_swap_enabled(false),
      cache_render_pass_contents(true),
      accelerated_animation_enabled(true),
      background_color_instead_of_checkerboard(false),
      show_overdraw_in_tracing(false),
      can_use_lcd_text(true),
      should_clear_root_render_pass(true),
      use_linear_fade_scrollbar_animator(false),
      scrollbar_linear_fade_delay_ms(300),
      scrollbar_linear_fade_length_ms(300),
      solid_color_scrollbars(false),
      solid_color_scrollbar_color(SK_ColorWHITE),
      solid_color_scrollbar_thickness_dip(-1),
      calculate_top_controls_position(false),
      use_memory_management(true),
      timeout_and_draw_when_animation_checkerboards(true),
      layer_transforms_should_scale_layer_contents(false),
      minimum_contents_scale(0.0625f),
      low_res_contents_scale_factor(0.125f),
      top_controls_height(0.f),
      top_controls_show_threshold(0.5f),
      top_controls_hide_threshold(0.5f),
      refresh_rate(60.0),
      max_partial_texture_updates(std::numeric_limits<size_t>::max()),
      num_raster_threads(1),
      default_tile_size(gfx::Size(256, 256)),
      max_untiled_layer_size(gfx::Size(512, 512)),
      minimum_occlusion_tracking_size(gfx::Size(160, 160)),
      use_pinch_zoom_scrollbars(false),
      use_pinch_virtual_viewport(false),
      // At 256x256 tiles, 128 tiles cover an area of 2048x4096 pixels.
      max_tiles_for_interest_area(128),
      max_unused_resource_memory_percentage(100),
      highp_threshold_min(0),
      force_direct_layer_drawing(false),
      strict_layer_property_change_checking(false),
      use_map_image(false),
      compositor_name("ChromiumCompositor"),
      ignore_root_layer_flings(false),
      always_overscroll(false),
      touch_hit_testing(true) {
  // TODO(danakj): Renable surface caching when we can do it more realiably.
  // crbug.com/170713
  cache_render_pass_contents = false;
  /// M: set num_raster_threads as property @{
  char value[PROPERTY_VALUE_MAX] = {'\0'};
  property_get("chromium.num_raster_threads", value, "1");
  int num = atoi(value);
  if (num >= 1) {
      LOG(INFO) << "[LayerTreeSettings] num_raster_threads is " << num;
      num_raster_threads = num;
  }
  /// @}
}

LayerTreeSettings::~LayerTreeSettings() {}

}  // namespace cc
