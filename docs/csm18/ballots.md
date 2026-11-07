# CSM 18 Ballots

All distinct ballots from the [CSM 18 analysis](/csm18), as in the official
`votes.blt` file, with candidate numbers replaced by names.
Each row is a group of identical ballots, with the number of ballots and the candidates in rank order.
There are 18,393 distinct ballots, ordered as in the source file.

[Source data](/data/csm18-ballots.csv)

Filter by candidate:
<input type="search" id="ballots-filter" class="ballots-filter" placeholder="Candidate name">

<p id="ballots-status"></p>

<table id="ballots-table" class="ballots-table" data-src="/data/csm18-ballots.csv">
  <thead></thead>
  <tbody></tbody>
</table>

<script src="/javascripts/ballots.js"></script>
