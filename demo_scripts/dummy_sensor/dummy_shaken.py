"""Dummy sensor script

Sends dummy data to BuildingDepot. This script sends noises to uuids[0] to [9],
and sends sin waves to uuids[10] to [13]. This emulates the situation where
a TI SensorTag is being shaken
"""

import time
import math
import random

from json_setting import JsonSetting
from buildingdepot_helper import BuildingDepotHelper

if __name__ == "__main__":
	'''Load settings
	If you installed BuildingDepot with its demo database, you do not need to
	change the parameters in connector_setting.json. Otherwise, update the
	file according to what you configured in BuildingDepot.
	'''
	connector_setting = JsonSetting('./connector_setting.json')
	bd_helper = BuildingDepotHelper()

	uuids = connector_setting.get('sensor_uuids')
	sampling_period = connector_setting.get('sampling_period')

	'Send dummy data'
	while True:
		data_array = []
		timestamp = time.time()
		value = math.sin(timestamp*4) * 10 + 10
		
		index = 0
		for uuid in uuids:
			dic = {}
			dic['sensor_id'] = uuid
			if index<10:
				dic['samples'] = [{"time":timestamp,"value":random.random()}]
			else:
				dic['samples'] = [{"time":timestamp,"value":value}]				
			dic['value_type']=''
			data_array.append(dic)
			index += 1

		result = bd_helper.post_data_array(data_array)
		print result
		time.sleep(sampling_period);
		
