/*
 * Copyright (C) 2008 Torch Mobile Inc. All rights reserved. (http://www.torchmobile.com/)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this library; see the file COPYING.LIB.  If not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301, USA.
 *
 */

#include "config.h"

#if ENABLE(WML)
#include "WMLDocument.h"

#include "core/history/BackForwardController.h"
#include "core/history/HistoryItem.h"
#include "core/page/Frame.h"
#include "core/page/Page.h"
#include "core/dom/ScriptableDocumentParser.h"
#include "WMLCardElement.h"
#include "WMLErrorHandling.h"
#include "WMLPageState.h"
#include "WMLTemplateElement.h"

/// M: @{
#include "WMLNames.h"
#include "core/wml/WMLInputElement.h"
#include "core/dom/Document.h"
/// @}

namespace WebCore {

WMLDocument::WMLDocument(const DocumentInit& initializer)
    : Document(initializer, WMLDocumentClass)
    , m_activeCard(0)
{
    clearXMLVersion();
    processViewport("width=device-width", ViewportArguments::XHTMLMobileProfile);
}

WMLDocument::~WMLDocument()
{
}

void WMLDocument::finishedParsing()
{
    if (ScriptableDocumentParser* parser = this->scriptableDocumentParser()) {
        if (!parser->wellFormed()) {
            Document::finishedParsing();
            return;
        }
    }

    bool hasAccess = initialize(true);
    Document::finishedParsing();

    if (!hasAccess) {
        m_activeCard = 0;

        WMLPageState* wmlPageState = wmlPageStateForDocument(this);
        if (!wmlPageState)
            return;

        Page* page = wmlPageState->page();
        if (!page)
            return;

        Frame* frame = page->mainFrame();
        if (!frame)
            return;

        wmlPageState->resetAccessControlData();
        wmlPageState->setPendingLoadType(FrameLoadTypeWMLDeckNotAccessible);
        HistoryItem* item = frame->loader()->history()->previousItem();
        if (!item)
            return;

        page->goToItem(item);
        return;
    }

    if (m_activeCard)
        m_activeCard->handleIntrinsicEventIfNeeded();
}

bool WMLDocument::initialize(bool aboutToFinishParsing)
{
    WMLPageState* wmlPageState = wmlPageStateForDocument(this);
    if (!wmlPageState || !wmlPageState->canAccessDeck())
        return false;

    // Remember that we'e successfully entered the deck
    wmlPageState->resetAccessControlData();

    // Notify the existance of templates to all cards of the current deck
    WMLTemplateElement::registerTemplatesInDocument(this);

    // Set destination card
    m_activeCard = WMLCardElement::determineActiveCard(this);
    if (!m_activeCard) {
        reportWMLError(this, WMLErrorNoCardInDocument);
        return true;
    }

    // Handle deck-level task overrides
    m_activeCard->handleDeckLevelTaskOverridesIfNeeded();

    // Handle card-level intrinsic event
    if (!aboutToFinishParsing)
        m_activeCard->handleIntrinsicEventIfNeeded();

    return true;
}

WMLPageState* wmlPageStateForDocument(Document* doc)
{
    ASSERT(doc);

    Page* page = doc->page();
    if (!page)
        return (WMLPageState*)0;

    return page->wmlPageState();
}

/// M: @{
String WMLDocument::conformTextToInputMask(const String& inputText)
{
    String text = inputText;
    Element* element = focusedElement();

    if (element && element->hasTagName(WMLNames::inputTag)) {
        WMLInputElement* wmlInputElement = toWMLInputElement(element);

        // get text that is conformed to the input format
        text = wmlInputElement->getTextConformedToInputMask(inputText);
    }

    return text;
}
/// @}

}

#endif
