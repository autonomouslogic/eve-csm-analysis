# Candidate Statistics

These measure how each candidate was ranked on the ballots, before any votes were transferred.
Ballots are weighted by their vote count, the same as everywhere else on the site.

## Ballots

This is the number of ballots ranking the candidate at any position.
The percentage is relative to the total number of votes cast.
A candidate who was ranked on nearly every ballot has a high number here, even if they were rarely ranked first.

## First Choice

This is the number of ballots ranking the candidate first.
The percentage is relative to the total number of votes cast.
Unlike [votes at election](/votes-at-election), this is the first preference only and does not include votes transferred
from other candidates.

## Mean Rank

This is the average rank the candidate was given, across only the ballots they appear on.
A lower number means the candidate was ranked higher on average.
Ballots not ranking the candidate are ignored, so a low mean on a small number of ballots can be misleading.
Compare it with [Ballots](#ballots).

## Median Rank

This is the middle rank the candidate was given, across only the ballots they appear on.
When the number of ballots is even, the two middle ranks are averaged, which can give a value ending in .5.
The median is less affected by a few very high or very low ranks than the mean.

## Jaccard Index

This measures how much two candidates share the same ballots, see [Jaccard index](/jaccard-index). It is shown as the
candidate overlap heatmap on each election page.
For a pair of candidates it is the number of ballots ranking both, divided by the number of ballots ranking either.
Ballots are weighted by their vote count, and a candidate's ballots are those ranking them at any position.

A value of 1 means the two candidates were on exactly the same ballots.
A value of 0 means no ballot ranked both.
Values in between show partial overlap, so a high value suggests voters who liked one candidate also tended to rank the other.
The diagonal is always 1, since a candidate is compared with themselves.
