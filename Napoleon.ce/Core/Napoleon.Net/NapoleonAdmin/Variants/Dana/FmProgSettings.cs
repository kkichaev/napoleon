using GRSoft.Network;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace GRSoft.NapoleonAdmin
{
    public partial class FmProgSettings : Form
    {
        static readonly string PROG_TYPE = "pda";

        static readonly string PHOTO_RES = "photoRes";
        static readonly string GPS_FREQ = "gpsFrequience";
        static readonly string GPS_DIST = "gpsDistance";
        static readonly string GPS_SEND_BK= "dataSendInBackground";
        static readonly string GPS_SEND_INTERVAL = "gpsSendInterval";
        static readonly string GPS_WAIT = "waitGpsCoordOnRequest";
        static readonly string GPS_VALID = "gps_valid_in_org";


        Agent agent;
        SimpleDataSet<ProgramSettings>  settings;
        List<ProgramSettings> progSettings;
        DBConnection conn;

        public FmProgSettings()
        {
            InitializeComponent();
        }

        public void SetAgent(Agent a, SimpleDataSet<ProgramSettings> settings, DBConnection conn)
        {
            this.agent = a;
            this.settings = settings;
            this.conn = conn;
            Text = "Настройки программы агента " + a.Name;

            progSettings = new List<ProgramSettings>();
            foreach(ProgramSettings ps in settings.Data)
            {
                if(ps.userid == a.id && ps.type == PROG_TYPE)
                {
                    progSettings.Add(ps);
                }
            }

            if (progSettings.Count == 0)
                initDefault();
            updateForm();
        }

        void updateForm()
        {
            numCameraRes.Value = getValue(PHOTO_RES);
            numGpsFreq.Value = getValue(GPS_FREQ);
            numGpsDist.Value = getValue(GPS_DIST);
            numSendInterval.Value = getValue(GPS_SEND_INTERVAL);
            numGpsWait.Value = getValue(GPS_WAIT);
            numGpsValid.Value = getValue(GPS_VALID);

            cbGpsBckg.Checked = getValue(GPS_SEND_BK) != 0;
        }

        int getValue(string id)
        {
            foreach (ProgramSettings ps in progSettings)
                if (ps.id == id)
                    return int.Parse(ps.value);

            return 0;
        }

        void setValue(decimal value, string id)
        {
            foreach (ProgramSettings ps in progSettings)
                if (ps.id == id)
                {
                    ps.value = value.ToString();
                    break;
                }
        }

        void initDefault()
        {
            ProgramSettings ps = new ProgramSettings();
            ps.userid = agent.id;
            ps.id = PHOTO_RES;
            ps.type = PROG_TYPE;
            ps.value = "800";
            progSettings.Add(ps);

            ps = new ProgramSettings();
            ps.userid = agent.id;
            ps.id = GPS_FREQ;
            ps.type = PROG_TYPE;
            ps.value = "60";
            progSettings.Add(ps);

            ps = new ProgramSettings();
            ps.userid = agent.id;
            ps.id = GPS_DIST;
            ps.type = PROG_TYPE;
            ps.value = "100";
            progSettings.Add(ps);

            ps = new ProgramSettings();
            ps.userid = agent.id;
            ps.id = GPS_SEND_BK;
            ps.type = PROG_TYPE;
            ps.value = "1";
            progSettings.Add(ps);

            ps = new ProgramSettings();
            ps.userid = agent.id;
            ps.id = GPS_SEND_INTERVAL;
            ps.type = PROG_TYPE;
            ps.value = "30";
            progSettings.Add(ps);

            ps = new ProgramSettings();
            ps.userid = agent.id;
            ps.id = GPS_WAIT;
            ps.type = PROG_TYPE;
            ps.value = "60";
            progSettings.Add(ps);

            ps = new ProgramSettings();
            ps.userid = agent.id;
            ps.id = GPS_VALID;
            ps.type = PROG_TYPE;
            ps.value = "5";
            progSettings.Add(ps);
        }

        private void button1_Click(object sender, EventArgs e)
        {
            setValue(numCameraRes.Value,PHOTO_RES);
            setValue(numGpsFreq.Value,GPS_FREQ);
            setValue(numGpsDist.Value,GPS_DIST);
            setValue(numSendInterval.Value,GPS_SEND_INTERVAL);
            setValue(numGpsWait.Value,GPS_WAIT);
            setValue(numGpsValid.Value,GPS_VALID);
            setValue(cbGpsBckg.Checked ? 1 : 0, GPS_SEND_BK);

            SimpleDataSet<ProgramSettings> wr = new SimpleDataSet<ProgramSettings>(ProgramSettings.OBJECT_NAME, false);

            List<ProgramSettings> ast = new List<ProgramSettings>();
            foreach (ProgramSettings ps in settings.Data)
                ast.Add(ps);

            foreach(ProgramSettings ps in progSettings)
            {
                if (ast.Contains(ps) == false)
                    settings.Add(ps);
                wr.Add(ps);
            }

            List<IDataSet> wrs = new List<IDataSet>();
            wrs.Add(wr);

            if(DataModule.UpdateDataSet(wrs, null, null, conn))
            {
                Close();
            }
            else
            {
                MessageBox.Show("Ошибка при записи");
            }
        }
    }
}
