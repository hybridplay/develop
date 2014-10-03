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
	int maxIR = 500;
	
	int minValue, maxValue, centerValue;
	int realValue;
	int actualValue;
	float normActualValue;
	float normActualValueColumpio;
	float normCenterDelta = 0.20f;
	int barScale = 280;
    String sensorName;
    int type;
    float minStable, maxStable;
    boolean triggerMin, triggerMax;
    boolean applyHCalib = false, applyVCalib = false;
    float columpioMin = 0.0f, columpioMax = 1.0f;
    
    public float calibH = 0.0f, calibV = 0.0f;
    
    public Sensor(String sName, int minS, int maxS, int _type){
    	minValue = 1024;
    	maxValue = 0;
    	actualValue = 0;
    	normActualValue = 0.0f;
    	normActualValueColumpio = 0.0f;
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
    			
    			float colMinShift = columpioMin*(maxStable-minStable);
    			float colMaxShift = (maxStable-minStable)*(1-columpioMax);
    			float newMinStable = minStable+colMinShift;
    			float newMaxStable = maxStable-colMaxShift;

    			if(applyHCalib){
    				normActualValue = -calibH + (vx-minStable)/(maxStable-minStable)*(1.0f+calibH);
    				normActualValueColumpio = -calibH + (vx-newMinStable)/(newMaxStable-newMinStable)*(1.0f+calibH);
    			}else if(applyVCalib){
    				normActualValue = -calibV + (vx-minStable)/(maxStable-minStable)*(1.0f+calibV);
    				normActualValueColumpio = -calibV + (vx-newMinStable)/(newMaxStable-newMinStable)*(1.0f+calibV);
    			}else{
    				normActualValue = (vx-minStable)/(maxStable-minStable)*1.0f;
    				normActualValueColumpio = (vx-newMinStable)/(newMaxStable-newMinStable)*1.0f;
    			}

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
    		/*if(vx >= 200){ // SAFE VALUE, NOISE BELOW
	    		if(vx <= maxStable && vx >= minStable){
	    			distanceIR = getDistanceCM(Math.abs(Math.round(normActualValue*254)));
				}else{
					distanceIR = 0;
				}
    		}*/
    		
    		if(realValue > -200 && realValue < 0){
    			distanceIR = 1;
    		}else if(realValue >= 0 && realValue <= maxIR){
    			distanceIR = 0;
    		}else if(realValue > maxIR && realValue < 600){
    			distanceIR = 1;
    		}
    		
    		//logData(realValue);
    	}
    }
    
    public void applyHCalibration(){
    	applyHCalib = true;
    	applyVCalib = false;
    }
    
    public void applyVCalibration(){
    	applyHCalib = false;
    	applyVCalib = true;
    }
    
    public void getCalibration(int cH, int cV){
    	float tempNormH = (cH-minStable)/(maxStable-minStable)*1.0f;
    	float tempNormV = (cV-minStable)/(maxStable-minStable)*1.0f;
    	
    	calibH = tempNormH;
    	calibV = tempNormV;
    	
    }
    
    public void getColumpioCalibration(float min, float max){
    	columpioMin = min;
    	columpioMax = max;
    }
    
    public void logData(int v){
    	Log.d("SENSOR",maxIR+" - IR RAW: "+v);
    }
    
    public Canvas draw(Canvas mCanvas, Paint paint, int mColor, int xDrawing, int yDrawing, int size) {
    	  paint.setColor(mColor);
          paint.setStyle(Paint.Style.STROKE); 
          paint.setStrokeWidth(2.5f);
          paint.setTextSize(26);
          
          if(type == 1){ // IR sensor
        	  if(distanceIR != 0){
        		  paint.setStyle(Paint.Style.FILL);
        	  }else{
        		  paint.setStyle(Paint.Style.STROKE);
        	  }
        	  mCanvas.drawRect(xDrawing, yDrawing, xDrawing + size, yDrawing + size, paint);
          }else{
        	  mCanvas.drawRect(xDrawing, yDrawing, xDrawing + size, yDrawing + normActualValue*barScale, paint);
        	  paint.setStyle(Paint.Style.FILL);
        	  if(triggerMax){
        		  mCanvas.drawRect(xDrawing, yDrawing - 30 + normActualValue*barScale, xDrawing + size, yDrawing + normActualValue*barScale, paint);
        	  }else if(triggerMin){
        		  mCanvas.drawRect(xDrawing, yDrawing, xDrawing + size, yDrawing + 30, paint);
        	  }
        	  mCanvas.drawText(getSensorName()+": "+String.format("%.1f", getDegrees()), xDrawing, yDrawing + size + barScale, paint);
          }

		return mCanvas;
    }
    
    public Canvas drawColumpio(Canvas mCanvas, Paint paint, int mColor, int xDrawing, int yDrawing, int size) {
  	  paint.setColor(mColor);
        paint.setStyle(Paint.Style.STROKE); 
        paint.setStrokeWidth(2.5f);
        paint.setTextSize(26);
        
        mCanvas.drawRect(xDrawing, yDrawing, xDrawing + size, yDrawing + normActualValueColumpio*barScale, paint);
        paint.setStyle(Paint.Style.FILL);
        mCanvas.drawText(getSensorName()+": "+String.format("%.1f", getDegrees()), xDrawing, yDrawing + size + barScale, paint);

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
	
	public float getDegreesColumpio(){
		return (normActualValueColumpio*180) - 90.0f;
	}
	
	public void setMaxIR(int mIR){
		maxIR = mIR;
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
