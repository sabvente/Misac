package misac.model.Elements;


public class Metal extends Element {
	private static final long serialVersionUID = 1L;

	public Metal() {
		elementType = ElementType.Metal;
	}

	@Override
	public boolean isOvercome(ElementType other) {
		if(other == ElementType.Wood || other == ElementType.Base)
			return true;
		return false;
	}

	@Override
	public boolean isGenerate(ElementType other) {
		if(other == ElementType.Water)
			return true;
		return false;
	}

}
