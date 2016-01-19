package com.weijie.firerunning.map;

import java.util.ArrayList;  
import java.util.List;  
  
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.graphics.Path;  
import android.graphics.Point;  
import android.graphics.drawable.Drawable;  
import android.util.Log;  
  
import com.amap.mapapi.core.GeoPoint;  
import com.amap.mapapi.core.OverlayItem;  
import com.amap.mapapi.map.ItemizedOverlay;  
import com.amap.mapapi.map.MapView;  
import com.amap.mapapi.map.Projection;  


public class RouteItemizedOverlay extends ItemizedOverlay<OverlayItem>{ 
    private List<OverlayItem> list = new ArrayList<OverlayItem>();  
      
    public RouteItemizedOverlay(Drawable d) {  
        super(boundCenterBottom(d));          
    }  
  
    @Override  
    protected OverlayItem createItem(int id) {  
        return list.get(id);  
    }  
  
    @Override  
    public int size() {  
        return list.size();  
  
    }  
      
    public void draw(Canvas canvas, MapView mapView, boolean shadow) {  
  
  
        Projection proj = mapView.getProjection();  
        Path path = new Path();  
        Paint paint = new Paint();  
        paint.setAntiAlias(true);    
        paint.setColor(Color.BLUE);    
        paint.setStyle(Paint.Style.STROKE);    
        paint.setStrokeWidth(3);    
          
        for (int i = 0; i < this.size(); i++) {  
            OverlayItem item = (OverlayItem)createItem(i);  
            GeoPoint gp = item.getPoint();  
            Point mPoint = new Point();  
            proj.toPixels(gp, mPoint);  
  
            if(i == 0) {  
                path.moveTo(mPoint.x, mPoint.y);  
            }else{  
                path.lineTo(mPoint.x, mPoint.y);  
            }  
            //canvas.drawText("ca", mPoint.x, mPoint.y, new Paint());  
        }  
        path.close();  
  
        canvas.drawPath(path, paint);  
  
        super.draw(canvas, mapView, shadow);  
    }  
    public void addMyRoute(List<LocationPoint> points) {  
  
        for(LocationPoint point : points) {  
                GeoPoint gp = new GeoPoint((int)(point.getLatitude()*1E6),(int)(point.getLongitutde()*1E6));  
                OverlayItem myItem = new OverlayItem(gp, "title", "snippet");  
                list.add(myItem);  
                Log.d("add","add an item");  
                populate();  
        }  
  
    }  
  
}  