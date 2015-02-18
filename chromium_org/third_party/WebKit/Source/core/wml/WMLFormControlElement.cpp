/**
 * Copyright (C) 2009 Torch Mobile Inc. All rights reserved. (http://www.torchmobile.com/)
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
#include "WMLFormControlElement.h"

#include "core/rendering/RenderBox.h"
#include "core/rendering/RenderObject.h"
#include "core/rendering/style/RenderStyle.h"

namespace WebCore {

WMLFormControlElement::WMLFormControlElement(const QualifiedName& tagName, Document* document)
    : WMLElement(tagName, document)
    , m_valueMatchesRenderer(false)
    , m_wasChangedSinceLastFormControlChangeEvent(false) /// M:
{
    /// M: @{
    setHasCustomStyleCallbacks();
    /// @}
}

PassRefPtr<WMLFormControlElement> WMLFormControlElement::create(const QualifiedName& tagName, Document* document)
{
    return adoptRef(new WMLFormControlElement(tagName, document));
}

WMLFormControlElement::~WMLFormControlElement()
{
}

bool WMLFormControlElement::supportsFocus() const
{
    return true;
}

bool WMLFormControlElement::isFocusable() const
{
    if (!renderer() || !renderer()->isBox())
        return false;

    if (toRenderBox(renderer())->size().isEmpty())
        return false;
    
    return WMLElement::isFocusable();
}
    
/// M: @{
bool WMLFormControlElement::wasChangedSinceLastFormControlChangeEvent() const
{
    return m_wasChangedSinceLastFormControlChangeEvent;
}

void WMLFormControlElement::setChangedSinceLastFormControlChangeEvent(bool changed)
{
    m_wasChangedSinceLastFormControlChangeEvent = changed;
}

void WMLFormControlElement::dispatchFormControlChangeEvent()
{
    WMLElement::dispatchChangeEvent();
    setChangedSinceLastFormControlChangeEvent(false);
}
/// @}

void WMLFormControlElement::attach(const AttachContext& context)
{
    ASSERT(!attached());
    WMLElement::attach(context);

    // The call to updateFromElement() needs to go after the call through
    // to the base class's attach() because that can sometimes do a close
    // on the renderer.
    if (renderer())
        renderer()->updateFromElement();
}

void WMLFormControlElement::recalcStyle(StyleChange change)
{
    WMLElement::recalcStyle(change);

    if (renderer())
        renderer()->updateFromElement();
}

/// M: @{
static void updateFromElementCallback(Node* node)
{
    ASSERT_ARG(node, node->isElementNode());
    ASSERT_ARG(node, toElement(node)->isFormControlElement());
    if (RenderObject* renderer = node->renderer())
        renderer->updateFromElement();
}

void WMLFormControlElement::didRecalcStyle(StyleChange)
{
    // updateFromElement() can cause the selection to change, and in turn
    // trigger synchronous layout, so it must not be called during style recalc.
    if (renderer())
        queuePostAttachCallback(updateFromElementCallback, this);
}
/// @}
}

#endif
