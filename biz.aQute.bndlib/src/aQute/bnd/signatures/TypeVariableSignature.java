package aQute.bnd.signatures;

import java.util.Objects;

import aQute.lib.stringrover.StringRover;

public class TypeVariableSignature implements ReferenceTypeSignature, ThrowsSignature {
	public final String identifier;

	TypeVariableSignature(String identifier) {
		this.identifier = identifier;
	}

	@Override
	public int hashCode() {
		return 31 + identifier.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TypeVariableSignature)) {
			return false;
		}
		TypeVariableSignature other = (TypeVariableSignature) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {
		return "T" + identifier + ";";
	}

	static TypeVariableSignature parseTypeVariableSignature(StringRover signature) {
		assert signature.charAt(0) == 'T';
		int end = signature.indexOf(';', 1);
		String identifier = signature.substring(1, end);
		signature.increment(end + 1);
		return new TypeVariableSignature(identifier);
	}
}
