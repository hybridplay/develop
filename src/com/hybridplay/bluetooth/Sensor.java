package com.hybridplay.bluetooth;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Sensor {
	
	int[] transferFunctionLUT = {
			255, 127, 93, 77, 67, 60, 54, 50, 47, 44, 42, 40, 38, 36, 35, 34,
			32, 31, 30, 30, 29, 28, 27, 27, 26, 26, 25, 25, 24, 22, 20, 19,
			19, 18, 18, 17, 17, 17, 16, 16, 16, 15, 15, 15, 14, 14, 14, 13,
			13, 13, 13, 13, 12, 12, 12, 12, 12, 11, 11, 11, 11, 11, 11, 10,
			10, 10, 10, 10, 10, 10, 10, 9, 9, 9, 9, 9, 9, 9, 9, 9,
			8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 8, 7, 7, 7,
			7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 6, 6,
			6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6,
			6, 6, 6, 6, 6, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5,
			5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
			0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
	};
	int avgIR = 100;
	int distanceIR = 0;
	
	int minValue, maxValue, centerValue;
	int realValue;
	int actualValue;
	float normActualValue;
	float normCenterDelta = 0.20f;
	int barScale = 280;
    String sensorName;
    int type;
    float minStable, maxStable;
    boolean triggerMin, triggerMax;
    
    public Sensor(String sName, int minS, int maxS, int _type){
    	minValue = 1024;
    	maxValue = 0;
    	actualValue = 0;
    	normActualValue = 0.0f;
    	minStable = minS;
    	maxStable = maxS;
    	triggerMin = false;
    	triggerMax = false;
    	type = _type;
    	setSensorName(sName);
    }
    
    public void update(int x,int vx){
    	// MANUAL SENSOR VALUES CORRECTION
    	if(type == 0){
    		if(vx <= maxStable && vx >= minStable){
    			realValue = vx;
    			normActualValue = (vx-minStable)/(maxStable-minStable)*1.0f;

    			actualValue = x;
    			maxValue = Math.max(actualValue, maxValue);
    			minValue = Math.min(actualValue, minValue);
    			centerValue = (maxValue+minValue)/2;

    			if(normActualValue > (normCenterDelta+0.5)){
    				triggerMin = false;
    				triggerMax = true;
    			}else if(normActualValue < (0.5-normCenterDelta)){
    				triggerMin = true;
    				triggerMax = false;
    			}else{
    				triggerMin = false;
    				triggerMax = false;
    			}
    		}
    	}else{ // IR
    		realValue = vx;
    		normActualValue = (vx-minStable)/(maxStable-minStable)*1.0f;
    		if(vx <= maxStable && vx >= minStable){
    			distanceIR = getDistanceCM(Math.abs(Math.round(normActualValue*254)));
			}else{
				distanceIR = 0;
			}
    		
    		logData(realValue);
    	}
    }
    
    public void logData(int v){
    	Log.d("Testing Sensor",Math.abs(v)+" : "+distanceIR);
    }
    
    public Canvas draw(Canvas mCanvas, Paint paint, int mColor, int xDrawing, int yDrawing) {
    	  paint.setColor(mColor);
          paint.setStyle(Paint.Style.STROKE); 
          paint.setStrokeWidth(2.5f);
          
          if(type == 1){ // IR sensor
        	  if(distanceIR != 0){
        		  paint.setStyle(Paint.Style.FILL);
        	  }else{
        		  paint.setStyle(Paint.Style.STROKE);
        	  }
        	  mCanvas.drawRect(xDrawing, yDrawing, xDrawing + 30, yDrawing + 30, paint);
          }else{
        	  mCanvas.drawRect(xDrawing, yDrawing, xDrawing + 30, yDrawing + normActualValue*barScale, paint);
        	  paint.setStyle(Paint.Style.FILL);
        	  if(triggerMax){
        		  mCanvas.drawRect(xDrawing, yDrawing - 20 + normActualValue*barScale, xDrawing + 30, yDrawing + normActualValue*barScale, paint);
        	  }else if(triggerMin){
        		  mCanvas.drawRect(xDrawing, yDrawing, xDrawing + 30, yDrawing + 20, paint);
        	  }
        	  mCanvas.drawText(getSensorName()+": "+String.format("%.1f", getDegrees()), xDrawing, yDrawing + 30 + barScale, paint);
          }

		return mCanvas;
    }
    
    public float getNormalizedValue(){
    	return normActualValue;
    }
    
    public boolean getTriggerMin(){
    	return triggerMin;
    }
    
    public boolean getTriggerMax(){
    	return triggerMax;
    }

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}
	
	public float getDegrees(){
		return (normActualValue*180) - 90.0f;
	}
	
	public int getDistanceIR(){
		return distanceIR;
	}
	
	public int getDistanceCM(int value){
		int sum = 0;
		for(int i=0;i<avgIR-1;i++){
			sum += (transferFunctionLUT[Math.round(value/4)]);
		}
		return Math.round(sum/avgIR);
	}
	
	public boolean isCloser(int treshold){
		if(treshold > distanceIR){
			return true;
		}else{
			return false;
		}
	}
   
}
