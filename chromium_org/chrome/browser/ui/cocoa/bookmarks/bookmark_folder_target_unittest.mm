// Copyright (c) 2012 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

#include "chrome/browser/bookmarks/bookmark_model.h"
#include "chrome/browser/bookmarks/bookmark_model_factory.h"
#import "chrome/browser/ui/cocoa/bookmarks/bookmark_bar_controller.h"
#import "chrome/browser/ui/cocoa/bookmarks/bookmark_bar_folder_controller.h"
#include "chrome/browser/ui/cocoa/bookmarks/bookmark_button.h"
#import "chrome/browser/ui/cocoa/bookmarks/bookmark_folder_target.h"
#include "chrome/browser/ui/cocoa/cocoa_profile_test.h"
#include "testing/gtest/include/gtest/gtest.h"
#include "testing/platform_test.h"
#import "third_party/ocmock/OCMock/OCMock.h"
#include "third_party/ocmock/gtest_support.h"

@interface OCMockObject(PreventRetainCycle)
- (void)clearRecordersAndExpectations;
@end

@implementation OCMockObject(PreventRetainCycle)

// We need a mechanism to clear the invocation handlers to break a
// retain cycle (see below; search for "retain cycle").
- (void)clearRecordersAndExpectations {
  [recorders removeAllObjects];
  [expectations removeAllObjects];
}

@end


class BookmarkFolderTargetTest : public CocoaProfileTest {
 public:
  virtual void SetUp() {
    CocoaProfileTest::SetUp();
    ASSERT_TRUE(profile());

    BookmarkModel* model = BookmarkModelFactory::GetForProfile(profile());
    bmbNode_ = model->bookmark_bar_node();
  }

  const BookmarkNode* bmbNode_;
};

TEST_F(BookmarkFolderTargetTest, StartWithNothing) {
  // Need a fake "button" which has a bookmark node.
  id sender = [OCMockObject mockForClass:[BookmarkButton class]];
  [[[sender stub] andReturnValue:OCMOCK_VALUE(bmbNode_)] bookmarkNode];

  // Fake controller
  id controller = [OCMockObject mockForClass:[BookmarkBarFolderController
                                               class]];
  // No current folder
  [[[controller stub] andReturn:nil] folderController];

  // Make sure we get an addNew
  [[controller expect] addNewFolderControllerWithParentButton:sender];

  base::scoped_nsobject<BookmarkFolderTarget> target(
      [[BookmarkFolderTarget alloc] initWithController:controller]);

  [target openBookmarkFolderFromButton:sender];
  EXPECT_OCMOCK_VERIFY(controller);
}

TEST_F(BookmarkFolderTargetTest, ReopenSameFolder) {
  // Need a fake "button" which has a bookmark node.
  id sender = [OCMockObject mockForClass:[BookmarkButton class]];
  [[[sender stub] andReturnValue:OCMOCK_VALUE(bmbNode_)] bookmarkNode];

  // Fake controller
  id controller = [OCMockObject mockForClass:[BookmarkBarFolderController
                                               class]];
  // YES a current folder.  Self-mock that as well, so "same" will be
  // true.  Note this creates a retain cycle in OCMockObject; we
  // accomodate at the end of this function.
  [[[controller stub] andReturn:controller] folderController];
  [[[controller stub] andReturn:sender] parentButton];

  // The folder is open, so a click should close just that folder (and
  // any subfolders).
  [[controller expect] closeBookmarkFolder:controller];

  base::scoped_nsobject<BookmarkFolderTarget> target(
      [[BookmarkFolderTarget alloc] initWithController:controller]);

  [target openBookmarkFolderFromButton:sender];
  EXPECT_OCMOCK_VERIFY(controller);

  // Our use of OCMockObject means an object can return itself.  This
  // creates a retain cycle, since OCMock retains all objects used in
  // mock creation.  Clear out the invocation handlers of all
  // OCMockRecorders we used to break the cycles.
  [controller clearRecordersAndExpectations];
}

TEST_F(BookmarkFolderTargetTest, ReopenNotSame) {
  // Need a fake "button" which has a bookmark node.
  id sender = [OCMockObject mockForClass:[BookmarkButton class]];
  [[[sender stub] andReturnValue:OCMOCK_VALUE(bmbNode_)] bookmarkNode];

  // Fake controller
  id controller = [OCMockObject mockForClass:[BookmarkBarFolderController
                                               class]];
  // YES a current folder but NOT same.
  [[[controller stub] andReturn:controller] folderController];
  [[[controller stub] andReturn:nil] parentButton];

  // Insure the controller gets a chance to decide which folders to
  // close and open.
  [[controller expect] addNewFolderControllerWithParentButton:sender];

  base::scoped_nsobject<BookmarkFolderTarget> target(
      [[BookmarkFolderTarget alloc] initWithController:controller]);

  [target openBookmarkFolderFromButton:sender];
  EXPECT_OCMOCK_VERIFY(controller);

  // Break retain cycles.
  [controller clearRecordersAndExpectations];
}
