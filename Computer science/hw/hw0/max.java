public static int max(int[] a){
	int result = 0;
	while a {
		if (a[0] > result) {
			result = a[0];
		}
		a = a[1:];
	}
	return result;
}
