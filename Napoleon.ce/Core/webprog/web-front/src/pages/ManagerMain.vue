<template>
  <q-page class="page">
    <q-card class="page-content-full-hight">
      <div class="page-header">{{ $t("manager.title") }}</div>

      <div class="row">
        <div class="col">
          <apexchart type="bar" height="350" :options="chartOptions" :series="series"></apexchart>
        </div>
        <div class="col">
          <apexchart type="bar" height="350" :options="chartOptions" :series="series"></apexchart>
        </div>
      </div>

      <q-table
        :columns="columns"/>
    </q-card>
  </q-page>
</template>

<script setup>
import { useI18n } from "vue-i18n";
const i18n = useI18n();

const columns = [
  {
    name: "division",
    label: "Подраздление",
    align: "left",
    field: "division",
    sortable: true,
  },
  {
    name: "user",
    label: "Пользователь",
    align: "left",
    field: "user",
    sortable: true,
  },
  {
    name: "visits",
    label: "Визиты",
    align: "left",
    field: "visits",
    sortable: true,
  },
  {
    name: "sum",
    label: "Сумма",
    align: "left",
    field: "sum",
    sortable: true,
  },
  {
    name: "access",
    label: "Доступ",
    align: "left",
    field: "access",
    sortable: true,
  },
]
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
