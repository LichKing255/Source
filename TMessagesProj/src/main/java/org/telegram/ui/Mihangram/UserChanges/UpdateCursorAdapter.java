package org.telegram.ui.Supergram.UserChanges;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import org.telegram.ui.Components.BackupImageView;

public class UpdateCursorAdapter
  extends CursorAdapter
{
  private a dataBaseAccess = new a();
  
  public UpdateCursorAdapter(Context paramContext, Cursor paramCursor)
  {
    super(paramContext, paramCursor, 0);
  }
  
  public void bindView(View paramView, Context paramContext, Cursor paramCursor)
  {
    ((UpdateCell)paramView).setData(this.dataBaseAccess.a(paramCursor));
  }
  
  public View newView(Context paramContext, Cursor paramCursor, ViewGroup paramViewGroup)
  {
    return new UpdateCell(this.mContext, 10);
  }
  
  public class ViewHolder
  {
    BackupImageView avatarImageView;
    TextView tvNewValue;
    TextView tvOldValue;
    
    public ViewHolder() {}
  }
}


/* Location:              C:\Users\Armandl\Downloads\Compressed\dex2jar-2.0\classes-dex2jar.jar!\org\telegram\ui\Supergram\UserChanges\UpdateCursorAdapter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       0.7.1
 */