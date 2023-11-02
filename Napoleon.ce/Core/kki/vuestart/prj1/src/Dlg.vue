<template>
    <md-dialog-alert
      md-content="Your post has been deleted!"
      md-confirm-text="Cool!" />
</template>

<script>
  export default {
    name: 'DlgAlert',
    props: ["showDialog"],
  }
</script>