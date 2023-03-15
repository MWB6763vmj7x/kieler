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


/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>KGrid Placement Data</b></em>'.
 * <!-- end-user-doc -->
 *
 * <!-- begin-model-doc -->
 * Gives an element in a gridPlacement a sizeHint
 * (currently the content is clipped when it overlaps the specified size)
 * <!-- end-model-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.cau.cs.kieler.core.krendering.KGridPlacementData#getWidthHint <em>Width Hint</em>}</li>
 *   <li>{@link de.cau.cs.kieler.core.krendering.KGridPlacementData#getHeightHint <em>Height Hint</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKGridPlacementData()
 * @model
 * @generated
 */
public interface KGridPlacementData extends KDirectPlacementData {
    /**
     * Returns the value of the '<em><b>Width Hint</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Width Hint</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Width Hint</em>' attribute.
     * @see #setWidthHint(float)
     * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKGridPlacementData_WidthHint()
     * @model
     * @generated
     */
    float getWidthHint();

    /**
     * Sets the value of the '{@link de.cau.cs.kieler.core.krendering.KGridPlacementData#getWidthHint <em>Width Hint</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Width Hint</em>' attribute.
     * @see #getWidthHint()
     * @generated
     */
    void setWidthHint(float value);

    /**
     * Returns the value of the '<em><b>Height Hint</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Height Hint</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Height Hint</em>' attribute.
     * @see #setHeightHint(float)
     * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKGridPlacementData_HeightHint()
     * @model
     * @generated
     */
    float getHeightHint();

    /**
     * Sets the value of the '{@link de.cau.cs.kieler.core.krendering.KGridPlacementData#getHeightHint <em>Height Hint</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Height Hint</em>' attribute.
     * @see #getHeightHint()
     * @generated
     */
    void setHeightHint(float value);

} // KGridPlacementData
