using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonManager
{
   class UserFormEx : UserForm
   {
      static readonly string FOCUS_MATRIX_NAME = "<Фокусная матрица>";

      SimpleDataSet<MatrixOrder> orderedMatrix = new SimpleDataSet<MatrixOrder>(MatrixOrder.OBJECT_NAME, false);
      DataGridViewComboBoxColumn clmnType;
      DataSet<string, OrgTypeBinding> dsTypes;
      DataSet<string, OrgType> types;

      MatrixOrderEditor matrixEditor;

      static Matrix focusMatrix;

      public UserFormEx(Divisions owner)
         : base(owner)
      {
         matrixEditor = new MatrixOrderEditor();
         matrixEditor.Dock = DockStyle.Fill;

         matrixEditor.Location = new System.Drawing.Point(0, 0);
         matrixEditor.Name = "tvAgentMatrix";
         matrixEditor.Size = new System.Drawing.Size(466, 279);
         matrixEditor.TabIndex = 0;
         matrixEditor.tvAgentMatrix.AfterCheck += new System.Windows.Forms.TreeViewEventHandler(this.tvAgentMatrix_AfterCheck);
         matrixEditor.tvAgentMatrix.BeforeCheck += new System.Windows.Forms.TreeViewCancelEventHandler(this.tvAgentMatrix_BeforeCheck);
         matrixEditor.DataChanged += new EventHandler(matrixEditor_DataChanged);

         udMatrix.Controls.Clear();
         udMatrix.Controls.Add(matrixEditor);
         tvAgentMatrix = matrixEditor.tvAgentMatrix;
      
         types = (DataSet<string, OrgType>)DataModule.Get(OrgType.OBJECT_NAME) ??
            new DataSet<string, OrgType>(OrgType.OBJECT_NAME, true);

         dsTypes = (DataSet<string, OrgTypeBinding>)DataModule.Get(OrgTypeBinding.OBJECT_NAME) ??
            new DataSet<string, OrgTypeBinding>(OrgTypeBinding.OBJECT_NAME);

         clmnType = new DataGridViewComboBoxColumn();
         clmnType.DataPropertyName = "Type";
         clmnType.FillWeight = 30F;
         clmnType.HeaderText = "Тип точки";
         clmnType.Name = "clmnType";
         clmnType.DisplayStyle = DataGridViewComboBoxDisplayStyle.ComboBox;
         clmnType.DisplayMember = "Name";
         clmnType.ValueMember = "ID";

         dgvOrgs.Columns.Add(clmnType);
         dgvOrgs.EditMode = DataGridViewEditMode.EditOnEnter;
         dgvOrgs.DataError += dgvOrgs_DataError;
         dgvOrgs.CurrentCellDirtyStateChanged += dgvOrgs_CurrentCellDirtyStateChanged;

         if(focusMatrix == null)
         {
            focusMatrix = new Matrix();
            focusMatrix.name = FOCUS_MATRIX_NAME;
         }
      }

      void matrixEditor_DataChanged(object sender, EventArgs e)
      {
         SimpleDataSet<MatrixOrder> send = new SimpleDataSet<MatrixOrder>(MatrixOrder.OBJECT_NAME, false);
         MatrixOrder mo = new MatrixOrder();

         send.Add(mo);
         mo.userid = Agent.id;
         int order = 0;
         List<String> usedMatrix = new List<string>();
         foreach (ListViewItem lvi in matrixEditor.lvOrderedMatrix.Items)
         {
            MatrixOrder.Item item = lvi.Tag as MatrixOrder.Item;
            usedMatrix.Add(item.name);
            item.order = order++;
            mo.items.Add(item);
         }

         bool matrixChanged = false;
         foreach (TreeNode tn in matrixEditor.tvAgentMatrix.Nodes)
         {
            Matrix m = tn.Tag as Matrix;
            if (usedMatrix.Contains(m.name) && tn.Checked == false)
            {
               tn.Checked = true;
               matrixChanged = true;
            }
         }

         owner.AddReplacedSet(Agent.id, send);
         if (matrixChanged)
            owner.AddReplacedSet(Agent.id, GetAgentMatrixDataSet());
      }

      void dgvOrgs_CurrentCellDirtyStateChanged(object sender, EventArgs e)
      {
         if(dgvOrgs.CurrentCell.ColumnIndex == 1)
         {
            dgvOrgs.CommitEdit(DataGridViewDataErrorContexts.Commit);
            Org o = dgvOrgs.CurrentRow.DataBoundItem as Org;
            if (dsTypes.ContainsKey(o.id) == false)
            {
               OrgTypeBinding otb = new OrgTypeBinding();
               otb.id = o.id;
               otb.type = o.Type;
               dsTypes[o.id] = otb;
            }
            else
               dsTypes[o.id].type = o.Type;

            owner.AddWriteSet(dsTypes);
         }
      }

      void dgvOrgs_DataError(object sender, DataGridViewDataErrorEventArgs e)
      {
         
      }

      protected override void BeforeUpdateData(string userid, List<IDataSet> updSets)
      {
         if (types.Count == 0)
         {
            clmnType.Items.Clear();
            updSets.Add(types);
         }

         if (dsTypes.Count == 0)
            updSets.Add(dsTypes);
      
         orderedMatrix.Filter = "\"userid\"='" + userid + "'";
         updSets.Add(orderedMatrix);
      }

      protected override void DataLoaded()
      {
         if(clmnType.Items.Count == 0)
            foreach (OrgType i in types.Data)
               clmnType.Items.Add(i);

         foreach(Org o in dsOrg.Data)
         {
            OrgTypeBinding val;
            if(dsTypes.TryGetValue(o.id, out val))
               o.Type = val.type;
         }

         bool haveFocusMatrix = false;
         foreach(Matrix m in dsCommonMatrix.Data)
            if(m == focusMatrix)
            {
               haveFocusMatrix = true;
               break;
            }

         if (!haveFocusMatrix)
            dsCommonMatrix.Add(dsCommonMatrix.Count, focusMatrix);

         ListView.ListViewItemCollection lvc = matrixEditor.lvOrderedMatrix.Items;
         lvc.Clear();
         foreach (MatrixOrder mo in orderedMatrix.Data)
         {
            mo.items.Sort();
            foreach (MatrixOrder.Item mi in mo.items)
            {
               ListViewItem lvi = new ListViewItem(mi.name);
               lvi.Tag = mi;
               lvc.Add(lvi);
            }
         }
         matrixEditor.UpdateButtonsState();
      }
   }
}
