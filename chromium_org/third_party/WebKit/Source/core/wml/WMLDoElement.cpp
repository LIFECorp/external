/**
 * Copyright (C) 2008, 2009 Torch Mobile Inc. All rights reserved. (http://www.torchmobile.com/)
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
#include "WMLDoElement.h"

#include "core/dom/Attribute.h"
#include "core/dom/Event.h"
#include "core/dom/EventNames.h"
#include "core/dom/KeyboardEvent.h"
#include "core/history/BackForwardController.h"
#include "core/page/Page.h"
#include "core/rendering/wml/RenderWMLButton.h"
#include "HTMLNames.h"
#include "WMLCardElement.h"
#include "WMLDocument.h"
#include "WMLNames.h"
#include "WMLPageState.h"
#include "WMLTaskElement.h"
#include "WMLTimerElement.h"
#include "WMLVariables.h"

namespace WebCore {

using namespace WMLNames;

WMLDoElement::WMLDoElement(const QualifiedName& tagName, Document* doc)
    : WMLElement(tagName, doc)
    , m_task(0)
    , m_isActive(false)
    , m_isNoop(false)
    , m_isOptional(false)
{
}

PassRefPtr<WMLDoElement> WMLDoElement::create(const QualifiedName& tagName, Document* document)
{
    return adoptRef(new WMLDoElement(tagName, document));
}

void WMLDoElement::defaultEventHandler(Event* event)
{
    if (m_isOptional)
        return;

    if (event->type() == eventNames().keypressEvent) {
        WMLElement::defaultEventHandler(event);
        return;
    }

    if (event->type() != eventNames().clickEvent && event->type() != eventNames().keydownEvent)
        return;
             
    if (event->isKeyboardEvent()
        && static_cast<KeyboardEvent*>(event)->keyIdentifier() != "Enter")
        return;

    if (m_type == "accept" || m_type == "options") {
        if (m_task)
            m_task->executeTask();
    } else if (m_type == "prev") {
        ASSERT(document()->isWMLDocument());
        WMLDocument* document = static_cast<WMLDocument*>(this->document());

        WMLPageState* pageState = wmlPageStateForDocument(document);
        if (!pageState)
            return;
    
        // Stop the timer of the current card if it is active
        if (WMLCardElement* card = document->activeCard()) {
            if (WMLTimerElement* eventTimer = card->eventTimer())
                eventTimer->stop();
        }

        pageState->page()->backForward()->goBack();
    } else if (m_type == "reset") {
        WMLPageState* pageState = wmlPageStateForDocument(document());
        if (!pageState)
            return;

        pageState->reset();
    }
}

void WMLDoElement::parseAttribute(const QualifiedName& name, const AtomicString& value)
{
    if (name == HTMLNames::typeAttr)
        m_type = parseValueForbiddingVariableReferences(value);
    else if (name == HTMLNames::nameAttr)
        m_name = parseValueForbiddingVariableReferences(value);
    else if (name == optionalAttr)
        m_isOptional = (value == "true");
    else
        WMLElement::parseAttribute(name, value);
}

Node::InsertionNotificationRequest WMLDoElement::insertedInto(ContainerNode* insertionPoint)
{
    WMLElement::insertedInto(insertionPoint);

    // Spec: An unspecified 'name' defaults to the value of the 'type' attribute.
    if (!hasAttribute(HTMLNames::nameAttr))
        m_name = m_type;

    ContainerNode* parent = parentNode();
    if (!parent || !parent->isWMLElement())
        return InsertionDone;

    if (WMLEventHandlingElement* eventHandlingElement = toWMLEventHandlingElement(static_cast<WMLElement*>(parent)))
        eventHandlingElement->registerDoElement(this, document());

    return InsertionDone;
}

void WMLDoElement::removedFrom(ContainerNode* insertionPoint)
{
    if (insertionPoint && insertionPoint->isWMLElement()) {
        if (WMLEventHandlingElement* eventHandlingElement = toWMLEventHandlingElement(static_cast<WMLElement*>(insertionPoint)))
            eventHandlingElement->deregisterDoElement(this);
    }

    WMLElement::removedFrom(insertionPoint);
}


void WMLDoElement::attach(const AttachContext& context)
{
    WMLElement::attach(context);

    // The call to updateFromElement() needs to go after the call through
    // to the base class's attach() because that can sometimes do a close
    // on the renderer.
    if (renderer())
        renderer()->updateFromElement();
}

RenderObject* WMLDoElement::createRenderer(RenderStyle* style)
{
    if (!m_isActive || m_isOptional || m_isNoop)
        return 0;

    if (style) {
        style->setUnique();
        style->setBackgroundColor(Color::lightGray);
    }

    return new RenderWMLButton(this);
}

void WMLDoElement::recalcStyle(StyleChange change)
{
    WMLElement::recalcStyle(change);

    if (renderer())
        renderer()->updateFromElement();
}

void WMLDoElement::registerTask(WMLTaskElement* task)
{
    ASSERT(!m_task);
    m_task = task;
}

void WMLDoElement::deregisterTask(WMLTaskElement* task)
{
    ASSERT_UNUSED(task, m_task == task);
    m_task = 0;
}

String WMLDoElement::label() const
{
    return parseValueSubstitutingVariableReferences(getAttribute(HTMLNames::labelAttr));
}

}

#endif
