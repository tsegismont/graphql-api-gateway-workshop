<template>
  <div class="row">
    <h3>Tracks</h3>
    <table class="table">
      <thead class="thead-light">
      <tr>
        <th scope="col">#</th>
        <th scope="col">Name</th>
      </tr>
      </thead>
      <tbody>
      <tr v-for="track in album.tracks" v-bind:key="track.number">
        <th scope="row">{{ track.number }}</th>
        <td>{{ track.name }}</td>
      </tr>
      </tbody>
    </table>
  </div>
</template>

<script>
    export default {
        name: 'TrackList',
        props: {
            album: Object
        }
    }
</script>
