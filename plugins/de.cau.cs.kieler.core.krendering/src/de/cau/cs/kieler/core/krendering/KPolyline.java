/**
 * KIELER - Kiel Integrated Environment for Layout Eclipse RichClient
 * 
 * http://www.informatik.uni-kiel.de/rtsys/kieler/
 * 
 * Copyright 2012 by
 * + Christian-Albrechts-University of Kiel
 *   + Department of Computer Science
 *     + Real-Time and Embedded Systems Group
 * 
 * This code is provided under the terms of the Eclipse Public License (EPL).
 * See the file epl-v10.html for the license text.
 */
package de.cau.cs.kieler.core.krendering;

import org.eclipse.emf.common.util.EList;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>KPolyline</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Creates a polyline between two or more points
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.cau.cs.kieler.core.krendering.KPolyline#getPoints <em>Points</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKPolyline()
 * @model
 * @generated
 */
public interface KPolyline extends KContainerRendering {
    /**
     * Returns the value of the '<em><b>Points</b></em>' containment reference list.
     * The list contents are of type {@link de.cau.cs.kieler.core.krendering.KPosition}.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Points</em>' containment reference list isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Points</em>' containment reference list.
     * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKPolyline_Points()
     * @model containment="true"
     * @generated
     */
    EList<KPosition> getPoints();

} // KPolyline
