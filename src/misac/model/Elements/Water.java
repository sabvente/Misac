package misac.model.Elements;


public class Water extends Element {
	private static final long serialVersionUID = 1L;

	public Water() {
		elementType = ElementType.Water;
	}

	@Override
	public boolean isOvercome(ElementType other) {
		if(other == ElementType.Fire || other == ElementType.Base)
			return true;
		return false;
	}

	@Override
	public boolean isGenerate(ElementType other) {
		if(other == ElementType.Wood)
			return true;
		return false;
	}

}
