package misac.model.Elements;


public class Tree extends Element {
	private static final long serialVersionUID = 1L;

	public Tree() {
		elementType = ElementType.Wood;
	}

	@Override
	public boolean isOvercome(ElementType other) {
		if(other == ElementType.Earth || other == ElementType.Base)
			return true;
		return false;
	}

	@Override
	public boolean isGenerate(ElementType other) {
		if(other == ElementType.Fire)
			return true;
		return false;
	}

}
