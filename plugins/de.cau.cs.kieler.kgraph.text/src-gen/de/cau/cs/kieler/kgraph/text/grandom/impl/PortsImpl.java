/**
 * generated by Xtext 2.10.0
 */
package de.cau.cs.kieler.kgraph.text.grandom.impl;

import de.cau.cs.kieler.kgraph.text.grandom.ConstraintType;
import de.cau.cs.kieler.kgraph.text.grandom.DoubleQuantity;
import de.cau.cs.kieler.kgraph.text.grandom.Flow;
import de.cau.cs.kieler.kgraph.text.grandom.GrandomPackage;
import de.cau.cs.kieler.kgraph.text.grandom.Ports;
import de.cau.cs.kieler.kgraph.text.grandom.Size;

import java.util.Collection;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.NotificationChain;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.InternalEObject;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.eclipse.emf.ecore.util.EObjectContainmentEList;
import org.eclipse.emf.ecore.util.InternalEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Ports</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link de.cau.cs.kieler.kgraph.text.grandom.impl.PortsImpl#isLabels <em>Labels</em>}</li>
 *   <li>{@link de.cau.cs.kieler.kgraph.text.grandom.impl.PortsImpl#getReUse <em>Re Use</em>}</li>
 *   <li>{@link de.cau.cs.kieler.kgraph.text.grandom.impl.PortsImpl#getSize <em>Size</em>}</li>
 *   <li>{@link de.cau.cs.kieler.kgraph.text.grandom.impl.PortsImpl#getConstraint <em>Constraint</em>}</li>
 *   <li>{@link de.cau.cs.kieler.kgraph.text.grandom.impl.PortsImpl#getFlow <em>Flow</em>}</li>
 * </ul>
 *
 * @generated
 */
public class PortsImpl extends MinimalEObjectImpl.Container implements Ports
{
  /**
   * The default value of the '{@link #isLabels() <em>Labels</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isLabels()
   * @generated
   * @ordered
   */
  protected static final boolean LABELS_EDEFAULT = false;

  /**
   * The cached value of the '{@link #isLabels() <em>Labels</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #isLabels()
   * @generated
   * @ordered
   */
  protected boolean labels = LABELS_EDEFAULT;

  /**
   * The cached value of the '{@link #getReUse() <em>Re Use</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getReUse()
   * @generated
   * @ordered
   */
  protected DoubleQuantity reUse;

  /**
   * The cached value of the '{@link #getSize() <em>Size</em>}' containment reference.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSize()
   * @generated
   * @ordered
   */
  protected Size size;

  /**
   * The default value of the '{@link #getConstraint() <em>Constraint</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConstraint()
   * @generated
   * @ordered
   */
  protected static final ConstraintType CONSTRAINT_EDEFAULT = ConstraintType.FREE;

  /**
   * The cached value of the '{@link #getConstraint() <em>Constraint</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getConstraint()
   * @generated
   * @ordered
   */
  protected ConstraintType constraint = CONSTRAINT_EDEFAULT;

  /**
   * The cached value of the '{@link #getFlow() <em>Flow</em>}' containment reference list.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getFlow()
   * @generated
   * @ordered
   */
  protected EList<Flow> flow;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected PortsImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return GrandomPackage.Literals.PORTS;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public boolean isLabels()
  {
    return labels;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setLabels(boolean newLabels)
  {
    boolean oldLabels = labels;
    labels = newLabels;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GrandomPackage.PORTS__LABELS, oldLabels, labels));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public DoubleQuantity getReUse()
  {
    return reUse;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetReUse(DoubleQuantity newReUse, NotificationChain msgs)
  {
    DoubleQuantity oldReUse = reUse;
    reUse = newReUse;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GrandomPackage.PORTS__RE_USE, oldReUse, newReUse);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setReUse(DoubleQuantity newReUse)
  {
    if (newReUse != reUse)
    {
      NotificationChain msgs = null;
      if (reUse != null)
        msgs = ((InternalEObject)reUse).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - GrandomPackage.PORTS__RE_USE, null, msgs);
      if (newReUse != null)
        msgs = ((InternalEObject)newReUse).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - GrandomPackage.PORTS__RE_USE, null, msgs);
      msgs = basicSetReUse(newReUse, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GrandomPackage.PORTS__RE_USE, newReUse, newReUse));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public Size getSize()
  {
    return size;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public NotificationChain basicSetSize(Size newSize, NotificationChain msgs)
  {
    Size oldSize = size;
    size = newSize;
    if (eNotificationRequired())
    {
      ENotificationImpl notification = new ENotificationImpl(this, Notification.SET, GrandomPackage.PORTS__SIZE, oldSize, newSize);
      if (msgs == null) msgs = notification; else msgs.add(notification);
    }
    return msgs;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSize(Size newSize)
  {
    if (newSize != size)
    {
      NotificationChain msgs = null;
      if (size != null)
        msgs = ((InternalEObject)size).eInverseRemove(this, EOPPOSITE_FEATURE_BASE - GrandomPackage.PORTS__SIZE, null, msgs);
      if (newSize != null)
        msgs = ((InternalEObject)newSize).eInverseAdd(this, EOPPOSITE_FEATURE_BASE - GrandomPackage.PORTS__SIZE, null, msgs);
      msgs = basicSetSize(newSize, msgs);
      if (msgs != null) msgs.dispatch();
    }
    else if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GrandomPackage.PORTS__SIZE, newSize, newSize));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public ConstraintType getConstraint()
  {
    return constraint;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setConstraint(ConstraintType newConstraint)
  {
    ConstraintType oldConstraint = constraint;
    constraint = newConstraint == null ? CONSTRAINT_EDEFAULT : newConstraint;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, GrandomPackage.PORTS__CONSTRAINT, oldConstraint, constraint));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public EList<Flow> getFlow()
  {
    if (flow == null)
    {
      flow = new EObjectContainmentEList<Flow>(Flow.class, this, GrandomPackage.PORTS__FLOW);
    }
    return flow;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public NotificationChain eInverseRemove(InternalEObject otherEnd, int featureID, NotificationChain msgs)
  {
    switch (featureID)
    {
      case GrandomPackage.PORTS__RE_USE:
        return basicSetReUse(null, msgs);
      case GrandomPackage.PORTS__SIZE:
        return basicSetSize(null, msgs);
      case GrandomPackage.PORTS__FLOW:
        return ((InternalEList<?>)getFlow()).basicRemove(otherEnd, msgs);
    }
    return super.eInverseRemove(otherEnd, featureID, msgs);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case GrandomPackage.PORTS__LABELS:
        return isLabels();
      case GrandomPackage.PORTS__RE_USE:
        return getReUse();
      case GrandomPackage.PORTS__SIZE:
        return getSize();
      case GrandomPackage.PORTS__CONSTRAINT:
        return getConstraint();
      case GrandomPackage.PORTS__FLOW:
        return getFlow();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @SuppressWarnings("unchecked")
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case GrandomPackage.PORTS__LABELS:
        setLabels((Boolean)newValue);
        return;
      case GrandomPackage.PORTS__RE_USE:
        setReUse((DoubleQuantity)newValue);
        return;
      case GrandomPackage.PORTS__SIZE:
        setSize((Size)newValue);
        return;
      case GrandomPackage.PORTS__CONSTRAINT:
        setConstraint((ConstraintType)newValue);
        return;
      case GrandomPackage.PORTS__FLOW:
        getFlow().clear();
        getFlow().addAll((Collection<? extends Flow>)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case GrandomPackage.PORTS__LABELS:
        setLabels(LABELS_EDEFAULT);
        return;
      case GrandomPackage.PORTS__RE_USE:
        setReUse((DoubleQuantity)null);
        return;
      case GrandomPackage.PORTS__SIZE:
        setSize((Size)null);
        return;
      case GrandomPackage.PORTS__CONSTRAINT:
        setConstraint(CONSTRAINT_EDEFAULT);
        return;
      case GrandomPackage.PORTS__FLOW:
        getFlow().clear();
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case GrandomPackage.PORTS__LABELS:
        return labels != LABELS_EDEFAULT;
      case GrandomPackage.PORTS__RE_USE:
        return reUse != null;
      case GrandomPackage.PORTS__SIZE:
        return size != null;
      case GrandomPackage.PORTS__CONSTRAINT:
        return constraint != CONSTRAINT_EDEFAULT;
      case GrandomPackage.PORTS__FLOW:
        return flow != null && !flow.isEmpty();
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (labels: ");
    result.append(labels);
    result.append(", constraint: ");
    result.append(constraint);
    result.append(')');
    return result.toString();
  }

} //PortsImpl
