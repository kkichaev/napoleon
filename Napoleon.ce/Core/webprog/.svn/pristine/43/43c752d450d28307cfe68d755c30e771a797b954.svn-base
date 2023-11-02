<template>
  <q-page padding>
      <h3>Манагер Майн!</h3>
      <apexchart type="bar" height="350" :options="chartOptions" :series="series"></apexchart>

      <div class="row no-wrap" style="background-color: green; padding: 10px; min-height: 75px;">
        <div style="background-color: blue;">column 1</div>
        <div class="col-grow" style="background-color: brown;">column 2</div>
        <div class="col-shrink" style="background-color: yellow; min-width: 400px;">column 3 Lorem ipsum dolor sit amet, consectetur adipisicing elit. Praesentium quidem veniam ad eaque distinctio accusamus aliquam, voluptatibus omnis, incidunt aut commodi doloremque et minus unde odio quaerat cumque autem at.</div>
      </div>
  </q-page>
</template>

<script setup>
const series = [{
            data: [400, 430, 448, 470, 540, 580, 690, 1100, 1200, 1380]
          }]
const chartOptions = {
            chart: {
              type: 'bar',
              height: 350
            },
            plotOptions: {
              bar: {
                borderRadius: 4,
                horizontal: true,
              }
            },
            dataLabels: {
              enabled: false
            },
            xaxis: {
              categories: ['South Korea', 'Canada', 'United Kingdom', 'Netherlands', 'Italy', 'France', 'Japan',
                'United States', 'China', 'Germany'
              ],
            }
          }
</script >

<style scoped>

</style>
