echo 'copy files'
echo
ssh -T devteam@dev.aceteam.app <<'EOL'
   cd prog/webfront/dist/spa/
   tar -czf ../web.tgz ./
EOL
scp devteam@dev.aceteam.app:/home/devteam/prog/webfront/dist/web.tgz /srv/napoleon/dockers/
cd /srv/napoleon/dockers
sudo rm web.back.tgz
sudo tar -czf web.back.tgz -C /var/www/html/ ./
sudo tar -xf web.tgz -C /var/www/html/
echo 'done'

