/*  GREATIS FORM DESIGNER FOR .NET           */
/*  Copyright (C) 2004-2007 Greatis Software */
/*  http://www.greatis.com/dotnet/formdes/   */
/*  http://www.greatis.com/bteam.html        */

using System;
using System.Collections;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Windows.Forms;

using Greatis.FormDesigner;

namespace NFormEditor
{
   #region ToolboxControl class
   /// <summary>
   /// Summary description for ToolboxControl.
   /// </summary>
   public class ToolboxControl : System.Windows.Forms.UserControl, Greatis.FormDesigner.IToolboxView
   {
      private readonly string DefaultCategory = "General";
      private System.Windows.Forms.ImageList images;
      private System.ComponentModel.IContainer components;
      private NFormEditor.ToolboxList list;

      private ToolboxService toolboxService;

      private Hashtable toolboxItems; // Key - categoryName, Value - ArrayList<ToolboxItemWithImage>

      internal enum PictureIndex
      {
         piPlus = 0,
         piMinus,
         piArrow
      }

      internal enum CategoryState
      {
         Expanded,
         Collapsed
      }

      public ToolboxControl()
      {
         InitializeComponent();

         toolboxItems = new Hashtable();

         list.Click += new EventHandler(OnItemClick);
         list.DoubleClick += new EventHandler(OnItemDoubleClick);
         list.ItemDrag += new ItemDragEventHandler(OnItemDrag);

         AddCategory(DefaultCategory);

         toolboxService = new ToolboxService(this);
      }

      public Greatis.FormDesigner.Designer Designer
      {
         set 
         {
            toolboxService.Designer = value;
         }
         get 
         {
            return toolboxService.Designer;
         }
      }

      private void AddCategory(string name)
      {
         ToolboxItem group = new CategoryItem(name, true);
         ToolboxItem pointer = new PointerItem();

         list.Items.AddRange( new ToolboxItem[] { group, pointer } );

         toolboxItems[name] = new ArrayList();
      }

      private void CollapseCategory(int categoryItem)
      {
         ToolboxItem item = (ToolboxItem)list.Items[categoryItem];

         if( item.groupControl == false || (CategoryState)(item.tag) == CategoryState.Collapsed )
            return;

         item.tag = CategoryState.Collapsed;
         
         list.BeginUpdate();

         item.image = (int)PictureIndex.piPlus;
         list.Invalidate(list.GetItemRectangle(categoryItem));

         ArrayList categoryItems = (ArrayList)toolboxItems[item.text];

         categoryItem++;
         list.Items.RemoveAt(categoryItem); // remove Pointer
         int i=0;
         while( i < categoryItems.Count )
         {
            list.Items.RemoveAt(categoryItem);
            i++;
         }

         list.EndUpdate();
      }

      private void ExpandCategory(int categoryItem)
      {
         ToolboxItem item = (ToolboxItem)list.Items[categoryItem];

         if( item.groupControl == false || (CategoryState)(item.tag) == CategoryState.Expanded )
            return;

         item.tag = CategoryState.Expanded;
         
         list.BeginUpdate();

         item.image = (int)PictureIndex.piMinus;
         list.Invalidate(list.GetItemRectangle(categoryItem));

         ArrayList categoryItems = (ArrayList)toolboxItems[item.text];

         list.Items.Insert(++categoryItem, new PointerItem());

         int i=0;
         while( i < categoryItems.Count )
         {
            ToolboxItemWithImage tbItem = (ToolboxItemWithImage)categoryItems[i];            
            ToolboxItem newItem = new TBItem(tbItem.item.DisplayName, tbItem.image, tbItem.item);
            list.Items.Insert(++categoryItem, newItem);
            
            i++;
         }

         list.EndUpdate();
      }

      private int FindCategoryItem(string category)
      {
         int i=0;
         foreach(ToolboxItem item in list.Items)
         {
            if( item.groupControl && item.text == category )
               return i;
            i++;
         }
         return -1;
      }

      private void InitializeComponent()
      {
				this.components = new System.ComponentModel.Container();
				System.ComponentModel.ComponentResourceManager resources = new System.ComponentModel.ComponentResourceManager(typeof(ToolboxControl));
            this.list = new ToolboxList();
				this.images = new System.Windows.Forms.ImageList(this.components);
				this.SuspendLayout();
				// 
				// list
				// 
				this.list.BackColor = System.Drawing.SystemColors.Control;
				this.list.BorderStyle = System.Windows.Forms.BorderStyle.None;
				this.list.CurrentSelectedIndex = -1;
				this.list.Dock = System.Windows.Forms.DockStyle.Fill;
				this.list.DrawMode = System.Windows.Forms.DrawMode.OwnerDrawVariable;
				this.list.FrameColor = System.Drawing.Color.Black;
				this.list.GroupColor = System.Drawing.SystemColors.ControlDark;
				this.list.Images = this.images;
				this.list.ItemHeight = 22;
				this.list.ItemUnderMouseColor = System.Drawing.SystemColors.InactiveCaptionText;
				this.list.Location = new System.Drawing.Point(0, 0);
				this.list.Name = "list";
				this.list.SelectedItemColor = System.Drawing.Color.AliceBlue;
				this.list.SelectedItemUnderMouseColor = System.Drawing.SystemColors.InactiveCaption;
				this.list.Size = new System.Drawing.Size(136, 192);
				this.list.TabIndex = 0;
				// 
				// images
				// 
				this.images.ImageStream = ((System.Windows.Forms.ImageListStreamer)(resources.GetObject("images.ImageStream")));
				this.images.TransparentColor = System.Drawing.Color.Magenta;
				this.images.Images.SetKeyName(0, "");
				this.images.Images.SetKeyName(1, "");
				this.images.Images.SetKeyName(2, "");
				// 
				// ToolboxControl
				// 
				this.Controls.Add(this.list);
				this.Name = "ToolboxControl";
				this.Size = new System.Drawing.Size(136, 192);
				this.ResumeLayout(false);

      }

      /// <summary> 
      /// Clean up any resources being used.
      /// </summary>
      protected override void Dispose( bool disposing )
      {
         if( disposing )
         {
            if( toolboxService != null )
               toolboxService.Dispose();

            if( components != null )
               components.Dispose();
         }
         base.Dispose( disposing );
      }

      private void OnItemDrag(object sender, ItemDragEventArgs arg)
      {
         if( arg.item.groupControl == true )
            return;

         System.Drawing.Design.ToolboxItem tag = arg.item.tag as System.Drawing.Design.ToolboxItem;
         if( tag != null && BeginDragAndDrop != null )
            BeginDragAndDrop(this, null);
      }

      private void OnItemDoubleClick(object sender, EventArgs e)
      {
         ToolboxItem tb = list.CurrentSelected;
         
         if( tb.groupControl == true )
            return;

         System.Drawing.Design.ToolboxItem tag = tb.tag as System.Drawing.Design.ToolboxItem;
         if( tag != null && DropControl != null)
            DropControl(this, new EventArgs());
      }

      private void OnItemClick(object sender, EventArgs e)
      {
         int selected = list.SelectedIndex;
         if( selected < 0 )
            return;

         ToolboxItem selectedItem = (ToolboxItem)list.Items[selected];
         if( selectedItem.groupControl )
         {
            if( (CategoryState)selectedItem.tag == CategoryState.Expanded )
               CollapseCategory(selected);
            else
               ExpandCategory(selected);
         }
      }

      #region IToolboxView Members

      public void AddItem(System.Drawing.Design.ToolboxItem item, string category)
      {
         if( category == null )
            category = DefaultCategory;

         if( toolboxItems.ContainsKey(category) == false )
            AddCategory(category);

         ArrayList categoryItems = (ArrayList)toolboxItems[category];
         int imageIndex = images.Images.Add(item.Bitmap, item.Bitmap.GetPixel(0,0));

         categoryItems.Add(new ToolboxItemWithImage(item, imageIndex));

         int categoryIndex = FindCategoryItem(category);
         ToolboxItem newItem = new TBItem(item.DisplayName, imageIndex, item);
         list.Items.Insert(categoryIndex + 1 + categoryItems.Count, newItem);
      }

      public void RemoveItem(System.Drawing.Design.ToolboxItem item, string category)
      {
         if( category == null )
            category = DefaultCategory;

         if( toolboxItems.ContainsKey(category) == false )
            return;

         ArrayList categoryItems = (ArrayList)toolboxItems[category];
         int index = 0;
         foreach(ToolboxItemWithImage tbi in categoryItems)
         {
            if( tbi.item == item )
            {
               categoryItems.RemoveAt(index);

               int categoryIndex = FindCategoryItem(category);
               list.Items.RemoveAt(categoryIndex + 1 + index);

               break;
            }
            index++;
         }
      }

      public event System.EventHandler BeginDragAndDrop;

      public Cursor CurrentCursor
      {
         get
         {
            ToolboxItem item = list.CurrentSelected;
            if( item == null )
               return Cursors.Arrow;
            
            System.Drawing.Design.ToolboxItem tag = item.tag as System.Drawing.Design.ToolboxItem;
            return (tag == null) ? Cursors.Arrow : Cursors.Cross;
         }
      }

      public Greatis.FormDesigner.ToolboxCategoryCollection Items
      {
         get
         {
            ToolboxCategoryItem[] tbItems = new ToolboxCategoryItem[toolboxItems.Count];
            
            int i=0;
            foreach(DictionaryEntry de in toolboxItems)
            {
               tbItems[i].name = (string)de.Key;

               ArrayList categoryItems = de.Value as ArrayList;
               System.Drawing.Design.ToolboxItem[] ic = new System.Drawing.Design.ToolboxItem[categoryItems.Count];

               int idx = 0;
               foreach( ToolboxItemWithImage tiwi in categoryItems )
                  ic[idx++] = tiwi.item;

               tbItems[i].items = new System.Drawing.Design.ToolboxItemCollection(ic);
            }

            return new Greatis.FormDesigner.ToolboxCategoryCollection(tbItems);
         }
      }

      public event System.EventHandler DropControl;

      public string SelectedCategory
      {
         get
         {
            int index = list.CurrentSelectedIndex;
            if( index < 0 )
               return null;

            ToolboxItem item;
            do
               item = (ToolboxItem)list.Items[index--];
            while( item.groupControl == false && index >= 0 );

            return (item.groupControl) ? item.text : null;
         }
         set
         {
            int index = FindCategoryItem(value);

            if( index < 0 )
               return;

            ToolboxItem item = (ToolboxItem)list.Items[index];
            if( (CategoryState)item.tag == CategoryState.Collapsed )
               this.ExpandCategory(index);
            
            list.CurrentSelectedIndex = index + 1;
         }
      }

      public System.Drawing.Design.ToolboxItem SelectedItem
      {
         get
         {
            ToolboxItem item = list.CurrentSelected;
            return (item!= null) ? item.tag as System.Drawing.Design.ToolboxItem : null;
         }
         set
         {
            if( value == null )
            {
               list.CurrentSelectedIndex = -1;
            }
         }
      }

      #endregion
   }

   internal class ToolboxItemWithImage
   {
      public System.Drawing.Design.ToolboxItem item;
      public int image;

      public ToolboxItemWithImage(System.Drawing.Design.ToolboxItem item, int image)
      {
         this.item = item;
         this.image = image;
      }
   }

   internal class ToolboxItem
   {
      public string text;
      public int image;
      public bool groupControl;
      public object tag;


      internal ToolboxItem(string text, int image, bool groupControl)
      {
         this.text = text;
         this.image = image;
         this.groupControl = groupControl;

         this.tag = null;
      }
   }

   internal class CategoryItem : ToolboxItem
   {
      public CategoryItem(string text, bool expanded) :
         base(text, (expanded) ? (int)ToolboxControl.PictureIndex.piMinus : (int)ToolboxControl.PictureIndex.piPlus , true)
      {
         tag = (expanded) ? ToolboxControl.CategoryState.Expanded : ToolboxControl.CategoryState.Collapsed;
      }
   }

   internal class TBItem : ToolboxItem
   {
      public TBItem(string text, int image, System.Drawing.Design.ToolboxItem item) :
         base(text, image, false)
      {
         tag = item;
      }
   }

   internal class PointerItem : ToolboxItem
   {
      public PointerItem() :
         base("Pointer", (int)ToolboxControl.PictureIndex.piArrow, false)
      {
      }
   }

   //internal class 
   #endregion
}
