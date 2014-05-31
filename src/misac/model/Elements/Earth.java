package misac.model.Elements;


public class Earth extends Element {
	private static final long serialVersionUID = 1L;

	public Earth() {
		elementType = ElementType.Earth;
	}


	@Override
	public boolean isOvercome(ElementType other) {
		if(other == ElementType.Water || other == ElementType.Base)
			return true;
		return false;
	}

	@Override
	public boolean isGenerate(ElementType other) {
		if(other == ElementType.Metal)
			return true;
		return false;
	}
}
