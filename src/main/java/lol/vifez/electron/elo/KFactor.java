package lol.vifez.electron.elo;

import lombok.Data;

@Data
public class KFactor {
	private final int startIndex, endIndex;
	private final double value;
}