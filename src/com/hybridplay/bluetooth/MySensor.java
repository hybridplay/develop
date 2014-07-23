package com.hybridplay.bluetooth;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


public class MySensor {
	
	private float actualValue, mediumValue, oldMediumValue;
	private ArrayList<Float> valueList;
	private float maxValue, minValue, centerValue;
	private static float minDistValue = 40;  //minimo para no tocar el centro
    private static float speedMinDistValue = 0.5f; //velocidad con la que se acercan al centro
    private float sensorStandardCenter = 512;
    
    private float   fireMax = 0, fireMin = 1024; //limites de disparo de accion
    private String sensorName;
    private boolean fireMaxActive = false;
    private boolean fireMinActive = false;
    private boolean fireCenter = true;
    private float distance;
    
    public MySensor(String name){
    	valueList = new ArrayList<Float>();
    	actualValue = 0;
    	mediumValue  = 0;
    	oldMediumValue = 0;
    	distance = 0;
    	maxValue = 0; 
    	minValue = 1024; 
    	centerValue =0;
    	setSensorName(name);
    }
    
    public void addValueList(float value){
    	if (valueList.size()>5){
    		valueList.remove(0);
    	}
    	valueList.add(value);
    	
    	double sum = 0;
        
    	for (int i = 0; i < valueList.size(); i++) {
            sum += valueList.get(i);
        }
    	mediumValue = (float) (sum / valueList.size());
        
    }
    
    public boolean isFireCenter() {
		return fireCenter;
	}

	public void setFireCenter(boolean fireCenter) {
		this.fireCenter = fireCenter;
	}

	public boolean isFireMaxActive() {
		return fireMaxActive;
	}

	public void setFireMaxActive(boolean fireMaxActive) {
		this.fireMaxActive = fireMaxActive;
	}

	public boolean isFireMinActive() {
		return fireMinActive;
	}

	public void setFireMinActive(boolean fireMinActive) {
		this.fireMinActive = fireMinActive;
	}

	public void update(float x){
    	actualValue = x;
    	
        //actulizamos los max y minimos
        if (actualValue > maxValue){
        	maxValue = actualValue;
        }else if(actualValue < minValue){
        	minValue = actualValue;
        }
        
        distance = maxValue - minValue;
        speedMinDistValue = distance / 10f;
        		
        //acercamos los max y min al centro en cada iteraccion
        if ((actualValue + minDistValue) < maxValue) maxValue = maxValue - speedMinDistValue;
        if (actualValue > (minValue + minDistValue)) minValue = minValue + speedMinDistValue;
        
        //calculamos el centro
        centerValue = minValue + ((maxValue - minValue) / 2);
        
        //calculamos los limites de los disparadores
        fireMax = centerValue + ((maxValue - centerValue) / 2);
        fireMin = minValue + ((centerValue - minValue) / 2);
        
        //comprobamos si estamos disaparando el max 
        if (actualValue > fireMax) {
        	fireMaxActive = true;
        	fireMinActive = false;
        }else if(actualValue < fireMin){
        	fireMaxActive = false;
        	fireMinActive = true;
        }else{
        	fireMaxActive = false;
        	fireMinActive = false;
        }
    }
    
    public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}
	

	public float getActualValue() {
		return actualValue;
	}

	public void setActualValue(float actualValue) {
		this.actualValue = actualValue;
	}
	
	public float getMaxValue() {
		return maxValue;
	}
	
	public float getCenterValue() {
		return centerValue;
	}

	public void setCenterValue(float cValue) {
		this.centerValue = cValue;
	}

	public float getStandardCenter(){
		return sensorStandardCenter;
	}
	
	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public Canvas draw(Canvas mCanvas, Paint paint, int mColor, int xDrawing, int yDrawing) {
    	
    	  paint.setColor(mColor);
          paint.setStyle(Paint.Style.STROKE); 
          paint.setStrokeWidth(2.5f);
          
          mCanvas.drawRect(xDrawing, yDrawing + 0, xDrawing + 30, yDrawing + actualValue, paint);

          if(fireMaxActive){
          	paint.setColor(Color.RED);
          	paint.setStyle(Paint.Style.FILL);
          	mCanvas.drawRect(xDrawing, yDrawing + fireMax, xDrawing + 30, yDrawing + maxValue, paint);
          	paint.setStyle(Paint.Style.STROKE);         	
          }
          
          if(fireMinActive){
          	paint.setColor(Color.RED);
          	paint.setStyle(Paint.Style.FILL);
          	mCanvas.drawRect(xDrawing , yDrawing + minValue, xDrawing + 30, yDrawing + fireMin, paint);
          	paint.setStyle(Paint.Style.STROKE);         	
          }
         
          //dibujamos todas las lineas
          paint.setColor(Color.RED);
          mCanvas.drawLine(xDrawing, yDrawing +  maxValue, xDrawing + 30, yDrawing +  maxValue, paint); //maximo
          paint.setColor(Color.GRAY);
          mCanvas.drawLine(xDrawing, yDrawing + fireMax, xDrawing + 30, yDrawing + fireMax, paint); //disparador maximo
          paint.setColor(Color.BLACK);
          mCanvas.drawLine(xDrawing, yDrawing + centerValue, xDrawing + 30, yDrawing + centerValue, paint); //centro
          paint.setColor(Color.GRAY);
          mCanvas.drawLine(xDrawing, yDrawing + fireMin, xDrawing + 30, yDrawing + fireMin, paint); //disparador minimo
          paint.setColor(Color.GREEN);
          mCanvas.drawLine(xDrawing, yDrawing + minValue, xDrawing + 30, yDrawing + minValue, paint); //minimo

		return mCanvas;
    }
	
	public void draw(Canvas canvas, Paint paint, int x1, int y1, int x2, int y2){
		//draw sensors
		//int h = canvas.getHeight(); //alto
		int w = canvas.getWidth(); //ancho
		float radius = w/30f;
		//int borde = w/10;
		//int distancia = w/8;
		paint.setAlpha((int) (distance + 50));

		//valor maximo sensor
		if (fireMaxActive){
			//paint.setColor(Color.argb(100, 255, 0, 0));
			paint.setStyle(Paint.Style.FILL);
			canvas.drawCircle(x1, y1, radius + distance/8, paint);//maximo
			//Log.d("log DISTANCE", Float.toString(distance));
		}else{
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(x1, y1, radius , paint);//valor maximo del sensor 
		}

		//valor minimo sensor
		if (fireMinActive){
			paint.setStyle(Paint.Style.FILL);
			canvas.drawCircle(x2, y2, radius + distance/8, paint);//valor minimo del sensor
			//Log.d("log DISTANCE", Float.toString(distance));
		}else{
			paint.setStyle(Paint.Style.STROKE);
			canvas.drawCircle(x2, y2, radius , paint);//valor minimo
		}

	}
	
	public float getMediumValue() {
		return mediumValue;
	}

	public void setMediumValue(float mediumValue) {
		this.mediumValue = mediumValue;
	}

	public float getOldMediumValue() {
		return oldMediumValue;
	}

	public void setOldMediumValue(float oldMediumValue) {
		this.oldMediumValue = oldMediumValue;
	}
   
}
