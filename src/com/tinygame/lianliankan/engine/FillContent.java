package com.tinygame.lianliankan.engine;
import java.util.LinkedList;
import java.util.Random;

import android.text.TextUtils;

import com.tinygame.lianliankan.ThemeManager;


public class FillContent {
	public static Random random = new Random();
	
	public static int[][] getRandomWithDiff(String diff, int factor) {
	    if (!TextUtils.isEmpty(diff)) {
	        String[] diffFactor = diff.split("x");
	        return getRandom(Integer.valueOf(diffFactor[0])
	                            , Integer.valueOf(diffFactor[1])
	                            , factor);
	    }
	    
	    return null;
	}
	
    public static int[][] getRandom(int width, int height, int factor) {
        int[][] result = new int[height][width];
        int[] numbers = new int[(width - 2) * (height - 2)];

        for (int i = 0; i < numbers.length; i++, i++) {
            int number = random.nextInt(factor) + 1;
            numbers[i] = number;
            numbers[i + 1] = number;
        }
        LinkedList<Integer> indexArray = new LinkedList<Integer>();
        for (int i = 0; i < numbers.length; i++) {
            indexArray.add(i);
        }
        
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                result[y][x] = ThemeManager.NO_IMAGE;
            }
        }
        
        for (int y = 1; y < (height - 1); y++) {
            for (int x = 1; x < (width - 1); x++) {
                int index = random.nextInt(indexArray.size());
                int value = indexArray.remove(index);
                result[y][x] = numbers[value];
            }
        }
        return result;
    }
	
    public static int[][] reArrange(int[][] arrays) {
        LinkedList<Integer> values = new LinkedList<Integer>();
        for (int y = 0; y < arrays.length; y++) {
            for (int x = 0; x < arrays[0].length; x++) {
                int value = arrays[y][x];
                if (value != ThemeManager.NO_IMAGE) {
                    values.add(value);
                }
            }
        }
        LinkedList<Integer> indexArray = new LinkedList<Integer>();
        for (int i = 0; i < values.size(); i++) {
            indexArray.add(i);
        }

        for (int y = 0; y < arrays.length; y++) {
            for (int x = 0; x < arrays[0].length; x++) {
                int valueInArray = arrays[y][x];
                if (valueInArray != ThemeManager.NO_IMAGE) {
                    int index = random.nextInt(indexArray.size());
                    int value = indexArray.remove(index);
                    arrays[y][x] = values.get(value);
                }
            }
        }
        return arrays;
    }
}
