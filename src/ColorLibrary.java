import java.awt.Color;
import java.io.Serializable;

/** Color Library.
 *  The colors used in the code.
 */
class ColorLibrary implements Serializable {
	private static final long serialVersionUID = 7335988726121101891L;
	Color colorTag;
	Color colorTagLabel;
	Color colorLink;
	Color colorAttributeName;
	Color colorAttributeValue;
	Color colorContent;
	Color colorComment;
	Color colorBackground;
	Color colorJsonAttributeName;
	Color colorJsonAttributeValue;
	Color colorCSSAttributeName;
	Color colorCSSAttributeValue;
	Color colorCSSVariable;
	Color colorCSSComment;
	Color colorComma;
	Color colorCodeRetain;
	Color colorCodeVariable;
	Color colorCodeFunction;
	Color colorCodeString;
	int indentSpace;
	int operationOnClickLink;
	int operationOnShowWebsite;
	int defaultBrowser;
	
	@Override
	public ColorLibrary clone() {
		ColorLibrary lib = new ColorLibrary();
		lib.colorTag = colorTag;
		lib.colorTagLabel = colorTagLabel;
		lib.colorLink = colorLink;
		lib.colorAttributeName = colorAttributeName;
		lib.colorAttributeValue = colorAttributeValue;
		lib.colorContent = colorContent;
		lib.colorComment = colorComment;
		lib.colorBackground = colorBackground;
		lib.colorJsonAttributeName = colorJsonAttributeName;
		lib.colorJsonAttributeValue = colorJsonAttributeValue;
		lib.colorCSSVariable = colorCSSVariable;
		lib.colorCSSComment = colorCSSComment;
		lib.colorComma = colorComma;
		lib.indentSpace = indentSpace;
		lib.colorCodeRetain = colorCodeRetain;
		lib.colorCodeVariable = colorCodeVariable;
		lib.colorCodeFunction = colorCodeFunction;
		lib.colorCSSAttributeName = colorCSSAttributeName;
		lib.colorCSSAttributeValue = colorCSSAttributeValue;
		lib.colorCodeString = colorCodeString;
		lib.operationOnClickLink = operationOnClickLink;
		lib.operationOnShowWebsite = operationOnShowWebsite;
		lib.defaultBrowser = defaultBrowser;
		return lib;
	}
	

}