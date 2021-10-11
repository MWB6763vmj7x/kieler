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
 * A representation of the model object '<em><b>KDecorator Placement Data</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#getLocation <em>Location</em>}</li>
 *   <li>{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#getXOffset <em>XOffset</em>}</li>
 *   <li>{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#getYOffset <em>YOffset</em>}</li>
 *   <li>{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#isRelative <em>Relative</em>}</li>
 *   <li>{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#getWidth <em>Width</em>}</li>
 *   <li>{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#getHeight <em>Height</em>}</li>
 * </ul>
 * </p>
 *
 * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKDecoratorPlacementData()
 * @model
 * @generated
 */
public interface KDecoratorPlacementData extends KPlacementData {
    /**
     * Returns the value of the '<em><b>Location</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Location</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Location</em>' attribute.
     * @see #setLocation(float)
     * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKDecoratorPlacementData_Location()
     * @model required="true"
     * @generated
     */
    float getLocation();

    /**
     * Sets the value of the '{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#getLocation <em>Location</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Location</em>' attribute.
     * @see #getLocation()
     * @generated
     */
    void setLocation(float value);

    /**
     * Returns the value of the '<em><b>XOffset</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>XOffset</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>XOffset</em>' attribute.
     * @see #setXOffset(float)
     * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKDecoratorPlacementData_XOffset()
     * @model
     * @generated
     */
    float getXOffset();

    /**
     * Sets the value of the '{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#getXOffset <em>XOffset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>XOffset</em>' attribute.
     * @see #getXOffset()
     * @generated
     */
    void setXOffset(float value);

    /**
     * Returns the value of the '<em><b>YOffset</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>YOffset</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>YOffset</em>' attribute.
     * @see #setYOffset(float)
     * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKDecoratorPlacementData_YOffset()
     * @model
     * @generated
     */
    float getYOffset();

    /**
     * Sets the value of the '{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#getYOffset <em>YOffset</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>YOffset</em>' attribute.
     * @see #getYOffset()
     * @generated
     */
    void setYOffset(float value);

    /**
     * Returns the value of the '<em><b>Relative</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Relative</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Relative</em>' attribute.
     * @see #setRelative(boolean)
     * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKDecoratorPlacementData_Relative()
     * @model required="true"
     * @generated
     */
    boolean isRelative();

    /**
     * Sets the value of the '{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#isRelative <em>Relative</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Relative</em>' attribute.
     * @see #isRelative()
     * @generated
     */
    void setRelative(boolean value);

    /**
     * Returns the value of the '<em><b>Width</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Width</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Width</em>' attribute.
     * @see #setWidth(float)
     * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKDecoratorPlacementData_Width()
     * @model required="true"
     * @generated
     */
    float getWidth();

    /**
     * Sets the value of the '{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#getWidth <em>Width</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Width</em>' attribute.
     * @see #getWidth()
     * @generated
     */
    void setWidth(float value);

    /**
     * Returns the value of the '<em><b>Height</b></em>' attribute.
     * <!-- begin-user-doc -->
     * <p>
     * If the meaning of the '<em>Height</em>' attribute isn't clear,
     * there really should be more of a description here...
     * </p>
     * <!-- end-user-doc -->
     * @return the value of the '<em>Height</em>' attribute.
     * @see #setHeight(float)
     * @see de.cau.cs.kieler.core.krendering.KRenderingPackage#getKDecoratorPlacementData_Height()
     * @model required="true"
     * @generated
     */
    float getHeight();

    /**
     * Sets the value of the '{@link de.cau.cs.kieler.core.krendering.KDecoratorPlacementData#getHeight <em>Height</em>}' attribute.
     * <!-- begin-user-doc -->
     * <!-- end-user-doc -->
     * @param value the new value of the '<em>Height</em>' attribute.
     * @see #getHeight()
     * @generated
     */
    void setHeight(float value);

} // KDecoratorPlacementData
