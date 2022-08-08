public static boolean threesumd(int[] a) {
	for(int i; i < len[a]; i++) {
		for(int j; j < len[a]; j++) {
			for(int k; k < len[a]; k++) {
				if (a[i] + a[k] + a[j] == 0 && i !=k && k !=j && i !=j) {
					return true;
				}
			}
		}
	}
	return false;
}