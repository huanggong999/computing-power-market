select interface_key,
       db_type,
       REPLACE(
               REPLACE(CAST(selection AS CHAR), CHAR(13), ' '),
           CHAR(10), ' '
       ) AS selection

from xdata.t_interface_open_config
where INTERFACE_TYPE = 1
  and status = 1;