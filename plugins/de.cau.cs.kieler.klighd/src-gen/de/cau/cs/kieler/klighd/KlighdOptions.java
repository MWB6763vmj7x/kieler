package de.cau.cs.kieler.klighd;

import de.cau.cs.kieler.klighd.IViewer;
import de.cau.cs.kieler.klighd.util.ExpansionAwareLayoutOption;
import java.util.EnumSet;
import org.eclipse.elk.core.data.ILayoutMetaDataProvider;
import org.eclipse.elk.core.data.LayoutOptionData;
import org.eclipse.elk.core.math.KVector;
import org.eclipse.elk.graph.properties.IProperty;
import org.eclipse.elk.graph.properties.Property;

@SuppressWarnings("all")
public class KlighdOptions implements ILayoutMetaDataProvider {
  /**
   * Default value for {@link #ELEMENT_IN_FOCUS}.
   */
  private final static boolean ELEMENT_IN_FOCUS_DEFAULT = false;
  
  /**
   * Whether an element is in the focus rather than in the context.
   */
  public final static IProperty<Boolean> ELEMENT_IN_FOCUS = new Property<Boolean>(
            "de.cau.cs.kieler.klighd.elementInFocus",
            ELEMENT_IN_FOCUS_DEFAULT,
            null,
            null);
  
  /**
   * Default value for {@link #TOOLTIP}.
   */
  private final static String TOOLTIP_DEFAULT = null;
  
  public final static IProperty<String> TOOLTIP = new Property<String>(
            "de.cau.cs.kieler.klighd.tooltip",
            TOOLTIP_DEFAULT,
            null,
            null);
  
  /**
   * Compound option containing pairs of layout option definitions for the collapsed and expanded state of a KNode
   */
  public final static IProperty<ExpansionAwareLayoutOption.ExpansionAwareLayoutOptionData> EXPANSION_AWARE_LAYOUT_OPTION = new Property<ExpansionAwareLayoutOption.ExpansionAwareLayoutOptionData>(
            "de.cau.cs.kieler.klighd.expansionAwareLayoutOption");
  
  public final static IProperty<KVector> MINIMAL_NODE_SIZE = new Property<KVector>(
            "de.cau.cs.kieler.klighd.minimalNodeSize");
  
  public final static IProperty<IViewer> VIEWER = new Property<IViewer>(
            "de.cau.cs.kieler.klighd.viewer");
  
  public void apply(final ILayoutMetaDataProvider.Registry registry) {
    registry.register(new LayoutOptionData(
        "de.cau.cs.kieler.klighd.elementInFocus",
        "",
        "Element in Focus",
        "Whether an element is in the focus rather than in the context.",
        ELEMENT_IN_FOCUS_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.BOOLEAN,
        boolean.class,
        EnumSet.of(LayoutOptionData.Target.PARENTS, LayoutOptionData.Target.NODES, LayoutOptionData.Target.PORTS, LayoutOptionData.Target.LABELS),
        LayoutOptionData.Visibility.ADVANCED
        , "klighd.labels.elementInFocus"
    ));
    registry.register(new LayoutOptionData(
        "de.cau.cs.kieler.klighd.tooltip",
        "",
        "Tooltip",
        null,
        TOOLTIP_DEFAULT,
        null,
        null,
        LayoutOptionData.Type.STRING,
        String.class,
        EnumSet.of(LayoutOptionData.Target.NODES),
        LayoutOptionData.Visibility.VISIBLE
        , "klighd.tooltip"
    ));
    registry.register(new LayoutOptionData(
        "de.cau.cs.kieler.klighd.expansionAwareLayoutOption",
        "",
        "Expansion Aware Options",
        "Compound option containing pairs of layout option definitions for the collapsed and expanded state of a KNode",
        null,
        null,
        null,
        LayoutOptionData.Type.OBJECT,
        ExpansionAwareLayoutOption.ExpansionAwareLayoutOptionData.class,
        null,
        LayoutOptionData.Visibility.HIDDEN
    ));
    registry.register(new LayoutOptionData(
        "de.cau.cs.kieler.klighd.minimalNodeSize",
        "",
        "Minimal Node Size",
        null,
        null,
        null,
        null,
        LayoutOptionData.Type.OBJECT,
        KVector.class,
        null,
        LayoutOptionData.Visibility.HIDDEN
    ));
    registry.register(new LayoutOptionData(
        "de.cau.cs.kieler.klighd.viewer",
        "",
        "Klighd Viewer",
        null,
        null,
        null,
        null,
        LayoutOptionData.Type.UNDEFINED,
        IViewer.class,
        null,
        LayoutOptionData.Visibility.HIDDEN
        , "klighd.viewer"
    ));
  }
}