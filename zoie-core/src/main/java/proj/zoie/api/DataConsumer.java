package proj.zoie.api;
/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.Collection;
import java.util.Comparator;

/**
 * interface for consuming a collection of data events
 */
public interface DataConsumer<D> {
	
	/**
	 * Data event abstraction.
	 */
	public static class DataEvent<D>
	{
		private D _data;
		private String _version;
    // This will override ZoieIndexable.isDeleted()
		private boolean _delete = false;
    private int _weight = 0;
				
		/**
		 * Create a data event instance
		 * @param version ZoieVersion of the event
		 * @param data Data for the event
		 */
		public DataEvent(D data, String version)
		{
			_data=data;
			_version=version;
		}
		
		/**
		 * Create a data event instance
		 * @param data Data for the event
		 * @param version ZoieVersion of the event
		 * @param del Is this event a delete event
		 */
		public DataEvent(D data, String version, boolean del)
		{
			_data=data;
			_version=version;
      _delete = del;
		}

    /**
     * Create a data event instance
     * @param data Data for the event
     * @param version ZoieVersion of the event
     * @param weight weight of data, typically size in bytes
     */
    public DataEvent(D data, String version, int weight)
    {
      _data=data;
      _version=version;
      _weight=weight;
    }

		/**
		 * Gets the version.
		 * @return ZoieVersion of the vent
		 */
		public String getVersion()
		{
			return _version;
		}
		
		/**
		 * Gets the data.
		 * @return Data for the event.
		 */
		public D getData()
		{
			return _data;
		}

		/**
		 * Gets the delete flag.
		 * @return Is this a delete event
		 */
		public boolean isDelete()
		{
			return _delete;
		}

    /**
     * Weight of this data event, typically size in bytes.
     * Used for memory control. See {@link proj.zoie.impl.indexing.AsyncDataConsumer}
     * @return data weight
     */
    public int getWeight()
    {
      return _weight;
    }
  }
	
	public static final class MarkerDataEvent<D> extends DataEvent<D>{

		private MarkerDataEvent(String version) {
			super(null, version);
		}
		
		public static <D> MarkerDataEvent<D> createMarkerEvent(String version){
			return new MarkerDataEvent<D>(version);
		}
	}
	
	/**
	 * Consumption of a collection of data events.
	 * Note that this method may have a side effect. That is it may empty the Collection passed in after execution.
	 * It is good practice if the data collection along with its contents passed to consume(data) never changed by client code later.
	 * We also strengthen the contract on this method that the events in data are sorted according to their version numbers and
	 * if consume is invoked on collection1 before on collection2, then all the max version number for events in collection1
	 * must be smaller than the min version number for events in collection2.
	 * @param data A collection of data to be consumed.
	 * @throws ZoieException
	 */
	void consume(Collection<DataEvent<D>> data) throws ZoieException;

	/**
	 * This method is not meant to be Thread-Safe, since that could add significant
	 * complexity and performance issue. When Thread-Safety is desired, the client
	 * should add external synchronization.
   * @return the version number of events that it has received but not necessarily processed.
   */
	String getVersion();

	/**
   * @return the version comparator.
   */
	Comparator<String> getVersionComparator();
}
